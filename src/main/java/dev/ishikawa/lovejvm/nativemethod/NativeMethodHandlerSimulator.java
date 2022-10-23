package dev.ishikawa.lovejvm.nativemethod;


import dev.ishikawa.lovejvm.nativemethod.implementation.ClassNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.DoubleNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.FloatNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.IntegerNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.LambdaFactoryNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.MethodHandleNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.ObjectNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.PrintStreamNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.StringNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.SystemNative;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class NativeMethodHandlerSimulator implements NativeMethodHandler {
  public static final NativeMethodHandler INSTANCE = new NativeMethodHandlerSimulator();

  private NativeMethodHandlerSimulator() {}

  /**
   * mimicing native method call. 1. remove args from currentFrame's locals 2. use the args to
   * calculate 3. return the answer
   */
  @Override
  public List<Word> handle(RawMethod rawMethod, Frame currentFrame) {
    return nativeMethodSimulations.stream()
        .filter(it -> it.isMatched(rawMethod))
        .findFirst()
        .map(it -> it.apply(currentFrame))
        .orElseThrow(
            () -> {
              var a = rawMethod;
              return new RuntimeException(
                  String.format(
                      "unhandled native method is used: %s%s",
                      rawMethod.getName().getLabel(), rawMethod.getDescriptor().getLabel()));
            });
  }

  private final List<NativeMethodSimulation> nativeMethodSimulations =
      List.of(
          new NativeMethodSimulation(
              "java/lang/Double", "longBitsToDouble", "(J)D", DoubleNative::longBitsToDouble),
          new NativeMethodSimulation(
              "java/lang/Double", "doubleToRawLongBits", "(D)J", DoubleNative::doubleToRawLongBits),
          new NativeMethodSimulation(
              "java/lang/Double", "toString", "(D)Ljava/lang/String;", DoubleNative::toString),
          new NativeMethodSimulation(
              "java/lang/Float", "floatToRawIntBits", "(F)I", FloatNative::floatToRawIntBits),
          new NativeMethodSimulation(
              "java/lang/Class",
              "forName0",
              "(Ljava/lang/String;ZLjava/lang/ClassLoader;Ljava/lang/Class;)Ljava/lang/Class;",
              ClassNative::forName0),
          new NativeMethodSimulation(
              "java/lang/Class",
              "getPrimitiveClass",
              "(Ljava/lang/String;)Ljava/lang/Class;",
              ClassNative::getPrimitiveClass),
          new NativeMethodSimulation(
              "java/lang/Object", "getClass", "()Ljava/lang/Class;", ObjectNative::getClass),
          new NativeMethodSimulation(
              "java/lang/System", "setOut0", "(Ljava/io/PrintStream;)V", SystemNative::setOut0),
          new NativeMethodSimulation(
              "java/lang/System", "setIn0", "(Ljava/io/InputStream;)V", SystemNative::setIn0),
          new NativeMethodSimulation(
              "java/lang/System", "setErr0", "(Ljava/io/PrintStream;)V", SystemNative::setErr0),
          new NativeMethodSimulation(
              "java/io/PrintStream", "write", "(Ljava/lang/String;)V", PrintStreamNative::write),
          new NativeMethodSimulation("java/lang/Object", "hashCode", "()I", ObjectNative::hashCode),
          new NativeMethodSimulation(
              "java/lang/Integer", "toString", "(I)Ljava/lang/String;", IntegerNative::toString),
          new NativeMethodSimulation(
              "java/lang/String", "intern", "()Ljava/lang/String;", StringNative::intern),
          new NativeMethodSimulation(
              "java/lang/invoke/MethodHandle",
              "invoke",
              "([Ljava/lang/Object;)Ljava/lang/Object;",
              MethodHandleNative::invoke),
          new NativeMethodSimulation(
              "jdk/internal/lambda/LambdaFactory",
              "createLambdaImplObject",
              "(Ljava/lang/invoke/MethodType;Ljava/lang/String;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/Object;",
              LambdaFactoryNative::createLambdaImplObject));

  private static class NativeMethodSimulation {
    @NotNull private final String binaryName;
    @NotNull private final String methodName;
    @NotNull private final String methodDesc;
    @NotNull private final Function<Frame, List<Word>> operation;

    public NativeMethodSimulation(
        @NotNull String binaryName,
        @NotNull String methodName,
        @NotNull String methodDesc,
        @NotNull Function<Frame, List<Word>> operation) {
      this.binaryName = binaryName;
      this.methodName = methodName;
      this.methodDesc = methodDesc;
      this.operation = operation;
    }

    public boolean isMatched(RawMethod rawMethod) {
      String binaryName = rawMethod.getClassBinaryName();
      String methodName = rawMethod.getName().getLabel();
      String methodDesc = rawMethod.getDescriptor().getLabel();

      return this.binaryName.equals(binaryName)
          && this.methodName.equals(methodName)
          && this.methodDesc.equals(methodDesc);
    }

    public List<Word> apply(Frame frame) {
      return operation.apply(frame);
    }
  }
}
