package dev.ishikawa.lovejvm.nativemethod.implementation;

import static dev.ishikawa.lovejvm.memory.stringpool.StringPoolSimulator.STRING_CLASS_LABEL;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class PrintStream {
  public static List<Word> printlnInt(Frame currentFrame) {
    int value = currentFrame.getOperandStack().pop().getValue();
    System.out.println(value);
    return List.of();
  }

  public static List<Word> printlnString(Frame currentFrame) {
    var stringObjectId = currentFrame.getOperandStack().pop().getValue();
    RawObject rawObject = RawSystem.heapManager.lookupObject(stringObjectId);

    RawClass stringRawClass =
        RawSystem.methodAreaManager.lookupOrLoadClass(STRING_CLASS_LABEL);
    var valueField =
        stringRawClass
            .findMemberFieldBy("value")
            .orElseThrow(() -> new RuntimeException("byte[] value field is not found"));

    int labelCharArrayId =
        RawSystem.heapManager.getValue(rawObject, valueField).get(0).getValue();
    RawObject labelObject = RawSystem.heapManager.lookupObject(labelCharArrayId);

    char[] labelCharArr = new char[labelObject.getArrSize()];
    for (int i = 0; i < labelObject.getArrSize(); i++) {
      labelCharArr[i] =
          (char) RawSystem.heapManager.getElement(labelObject, i).get(0).getValue();
    }
    String label = String.valueOf(labelCharArr);

    System.out.println(label);
    return List.of();
  }
}
