package dev.ishikawa.lovejvm.memory.stringpool;

import static dev.ishikawa.lovejvm.memory.stringpool.StringPoolSimulator.STRING_CLASS_LABEL;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class StringPoolUtil {
  /**
   * getLabelByObjectId returns label value by retrieving it from heap.
   * This is for String object not managed by StringPool.
   * @return label actual value of the object specified by stringObjectId.
   * */
  public static String getLabelByObjectId(int stringObjectId) {
    RawObject stringRawObject = RawSystem.heapManager.lookupObject(stringObjectId);
    RawClass stringRawClass = RawSystem.methodAreaManager.lookupOrLoadClass(STRING_CLASS_LABEL);

    var valueField =
        stringRawClass
            .findMemberFieldBy("value")
            .orElseThrow(() -> new RuntimeException("byte[] value field is not found"));

    int labelCharArrayId =
        RawSystem.heapManager.getValue(stringRawObject, valueField).get(0).getValue();
    RawObject labelObject = RawSystem.heapManager.lookupObject(labelCharArrayId);

    char[] labelCharArr = new char[labelObject.getArrSize()];
    for (int i = 0; i < labelObject.getArrSize(); i++) {
      labelCharArr[i] = (char) RawSystem.heapManager.getElement(labelObject, i).get(0).getValue();
    }

    return String.valueOf(labelCharArr);
  }
}
