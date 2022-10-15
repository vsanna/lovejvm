package dev.ishikawa.lovejvm.nativemethod;

import static dev.ishikawa.lovejvm.memory.stringpool.StringPoolSimulator.STRING_CLASS_LABEL;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.rawobject.RawObject;
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

    if (Objects.equals(binaryName, "java/lang/Double")
        && Objects.equals(methodName, "longBitsToDouble")
        && Objects.equals(methodDesc, "(J)D")) {
      // static
      var v2 = currentFrame.getOperandStack().pop().getValue();
      var v1 = currentFrame.getOperandStack().pop().getValue();
      double result = Double.longBitsToDouble(ByteUtil.concatToLong(v1, v2));
      return Word.of(result);
    }

    if (Objects.equals(binaryName, "java/lang/Double")
        && Objects.equals(methodName, "doubleToRawLongBits")
        && Objects.equals(methodDesc, "(D)J")) {
      var v2 = currentFrame.getOperandStack().pop().getValue();
      var v1 = currentFrame.getOperandStack().pop().getValue();
      long result =
          Double.doubleToRawLongBits(Double.longBitsToDouble(ByteUtil.concatToLong(v1, v2)));
      return Word.of(result);
    }

    if (Objects.equals(binaryName, "java/lang/Float")
        && Objects.equals(methodName, "floatToRawIntBits")
        && Objects.equals(methodDesc, "(F)I")) {
      var v1 = currentFrame.getOperandStack().pop().getValue();
      int result = Float.floatToRawIntBits(Float.intBitsToFloat(v1));
      return List.of(Word.of(result));
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

    if (Objects.equals(binaryName, "java/lang/System")
        && Objects.equals(methodName, "setOut0")
        && Objects.equals(methodDesc, "(Ljava/io/PrintStream;)V")) {
      var printStreamObjectId = currentFrame.getOperandStack().pop().getValue();
      var systemRawClass = RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/System");
      var outField =
          systemRawClass
              .findStaticFieldBy("out")
              .orElseThrow(() -> new RuntimeException("out field is not found"));

      RawSystem.methodAreaManager.putStaticFieldValue(
          systemRawClass, outField, List.of(Word.of(printStreamObjectId)));

      return List.of();
    }

    if (Objects.equals(binaryName, "java/io/PrintStream")
        && Objects.equals(methodName, "println")
        && Objects.equals(methodDesc, "(Ljava/lang/String;)V")) {
      var stringObjectId = currentFrame.getOperandStack().pop().getValue();
      RawObject rawObject = RawSystem.heapManager.lookupObject(stringObjectId);

      RawClass stringRawClass = RawSystem.methodAreaManager.lookupOrLoadClass(STRING_CLASS_LABEL);
      var valueField =
          stringRawClass
              .findMemberFieldBy("value")
              .orElseThrow(() -> new RuntimeException("byte[] value field is not found"));

      int labelCharArrayId =
          RawSystem.heapManager.getValue(rawObject, valueField).get(0).getValue();
      RawObject labelObject = RawSystem.heapManager.lookupObject(labelCharArrayId);

      char[] labelCharArr = new char[labelObject.getArrSize()];
      for (int i = 0; i < labelObject.getArrSize(); i++) {
        labelCharArr[i] = (char) RawSystem.heapManager.getElement(labelObject, i).get(0).getValue();
      }
      String label = String.valueOf(labelCharArr);

      // this System.out.println is host language's System.out.println
      System.out.println(label);
      return List.of();
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
}
