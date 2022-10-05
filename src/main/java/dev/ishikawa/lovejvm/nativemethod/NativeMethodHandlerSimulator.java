package dev.ishikawa.lovejvm.nativemethod;


import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.Objects;

public class NativeMethodHandlerSimulator implements NativeMethodHandler {
  public static final NativeMethodHandler INSTANCE = new NativeMethodHandlerSimulator();

  private NativeMethodHandlerSimulator() {}

  /**
   * mimicing native method call. 1. remove args from currentFrame's locals 2. use the args to
   * calculate 3. return the answer
   */
  @Override
  public List<Word> handle(RawMethod rawMethod, Frame currentFrame) {
    String binaryName = rawMethod.getClassBinaryName();
    String methodName = rawMethod.getName().getLabel();
    String methodDesc = rawMethod.getDescriptor().getLabel();

    if (Objects.equals(methodName, "longBitsToDouble") && Objects.equals(methodDesc, "(J)D")) {
      // static
      var v2 = currentFrame.getOperandStack().pop().getValue();
      var v1 = currentFrame.getOperandStack().pop().getValue();
      double result = Double.longBitsToDouble(ByteUtil.concatToLong(v1, v2));
      return Word.of(result);
    }

    if (Objects.equals(methodName, "floatToRawIntBits") && Objects.equals(methodDesc, "(F)I")) {
      var v1 = currentFrame.getOperandStack().pop().getValue();
      int result = Float.floatToRawIntBits(Float.intBitsToFloat(v1));
      return List.of(Word.of(result));
    }

    if (Objects.equals(methodName, "doubleToRawLongBits") && Objects.equals(methodDesc, "(D)J")) {
      var v2 = currentFrame.getOperandStack().pop().getValue();
      var v1 = currentFrame.getOperandStack().pop().getValue();
      long result =
          Double.doubleToRawLongBits(Double.longBitsToDouble(ByteUtil.concatToLong(v1, v2)));
      return Word.of(result);
    }

    if (Objects.equals(methodName, "forName0")
        && Objects.equals(
            methodDesc,
            "(Ljava/lang/String;ZLjava/lang/ClassLoader;Ljava/lang/Class;)Ljava/lang/Class;")) {
      // TODO
      return List.of(Word.of(-100));
    }

    if (Objects.equals(binaryName, "java/lang/Object")
        && Objects.equals(methodName, "getClass")
        && Objects.equals(methodDesc, "()Ljava/lang/Class;")) {
      var v1 = currentFrame.getOperandStack().pop().getValue();
      // TODO: array should have rawClass too
      var classObjectReference =
          Objects.requireNonNull(RawSystem.heapManager.lookupObject(v1).getRawClass())
              .getClassObjectId();
      return List.of(Word.of(classObjectReference));
    }

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
        return List.of(Word.of(0));
      case ARRAY:
        return List.of(Word.of(0));
      case OBJECT_REFERENCE:
        return List.of(Word.of(0));
      case VOID:
        return List.of();
      default:
        throw new RuntimeException(
            String.format("unexpected returning type: %s", rawMethod.getReturningType()));
    }
  }
}
