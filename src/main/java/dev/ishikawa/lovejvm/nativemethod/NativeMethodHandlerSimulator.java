package dev.ishikawa.lovejvm.nativemethod;


import dev.ishikawa.lovejvm.nativemethod.implementation.ClassNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.IntegerNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.ObjectNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.PrintStreamNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.StringNative;
import dev.ishikawa.lovejvm.nativemethod.implementation.SystemNative;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.util.ByteUtil;
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
              return new RuntimeException("unhandled native method is used");
            });
  }

  private List<Word> defaultNativeMethodHandler(RawMethod rawMethod) {
    switch (rawMethod.getReturningType()) {
      case BYTE:
      case SHORT:
      case CHAR:
      case FLOAT:
      case INT:
        return List.of(Word.of(0));
      case DOUBLE:
        return Word.of(0.0D);
      case LONG:
        return Word.of(0L);
      case BOOLEAN:
        return List.of(Word.of(false));
      case RETURN_ADDRESS:
      case ARRAY:
      case OBJECT_REFERENCE:
        return List.of(Word.of(0));
      case VOID:
        return List.of();
      default:
        throw new RuntimeException(
            String.format("unexpected returning type: %s", rawMethod.getReturningType()));
    }
  }

  private final List<NativeMethodSimulation> nativeMethodSimulations =
      List.of(
          new NativeMethodSimulation(
              "java/lang/Double",
              "longBitsToDouble",
              "(J)D",
              (currentFrame) -> {
                var v1 = currentFrame.getOperandStack().pop().getValue();
                var v2 = currentFrame.getOperandStack().pop().getValue();
                double result = Double.longBitsToDouble(ByteUtil.concatToLong(v1, v2));
                return Word.of(result);
              }),
          new NativeMethodSimulation(
              "java/lang/Double",
              "doubleToRawLongBits",
              "(D)J",
              (currentFrame) -> {
                var v1 = currentFrame.getOperandStack().pop().getValue();
                var v2 = currentFrame.getOperandStack().pop().getValue();
                long result = Double.doubleToRawLongBits(ByteUtil.concatToDouble(v1, v2));
                return Word.of(result);
              }),
          new NativeMethodSimulation(
              "java/lang/Float",
              "floatToRawIntBits",
              "(F)I",
              (currentFrame) -> {
                var v1 = currentFrame.getOperandStack().pop().getValue();
                int result = Float.floatToRawIntBits(ByteUtil.convertToFloat(v1));
                return List.of(Word.of(result));
              }),
          new NativeMethodSimulation(
              "java/lang/Class",
              "forName0",
              "(Ljava/lang/String;ZLjava/lang/ClassLoader;Ljava/lang/Class;)Ljava/lang/Class;",
              (currentFrame) -> {
                // TODO: impl
                return List.of(Word.of(-100));
              }),
          new NativeMethodSimulation(
              "java/lang/Object", "getClass", "()Ljava/lang/Class", ObjectNative::getClass),
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
              "java/lang/Class",
              "getPrimitiveClass",
              "(Ljava/lang/String;)Ljava/lang/Class;",
              ClassNative::getPrimitiveClass),
          new NativeMethodSimulation(
              "java/lang/String", "intern", "()Ljava/lang/String;", StringNative::intern));

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
