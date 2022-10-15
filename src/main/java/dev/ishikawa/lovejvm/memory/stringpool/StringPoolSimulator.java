package dev.ishikawa.lovejvm.memory.stringpool;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StringPoolSimulator implements StringPool {
  public final Map<String, RawObject> stringMap = new HashMap();
  private RawClass stringRawClass;

  /** @return objectId of the string object corresponding to the given label */
  @Override
  public int getOrCreate(String label) {
    return Optional.ofNullable(stringMap.get(label))
        .map(RawObject::getObjectId)
        .orElseGet(() -> register(label));
  }

  /**
   * create a String object,
   *
   * @return the newly created object's objectId
   */
  private int register(String label) {
    if (stringRawClass == null) {
      stringRawClass = RawSystem.methodAreaManager.lookupOrLoadClass(STRING_CLASS_LABEL);
    }

    int objectId = RawSystem.heapManager.register(stringRawClass);
    RawObject stringObject = RawSystem.heapManager.lookupObject(objectId);
    stringMap.put(label, stringObject);

    // TODO: initialize String class

    // set byte array of the label into value field of this String object
    var byteArrayClass = RawArrayClass.lookupOrCreatePrimaryRawArrayClass(JvmType.BYTE, 1);
    var arrayObjectId = RawSystem.heapManager.registerArray(byteArrayClass, label.length());
    var arrayObject = RawSystem.heapManager.lookupObject(arrayObjectId);
    for (int i = 0; i < label.getBytes().length; i++) {
      // TODO: 1byteに1word割り当ててしまっている. 直す
      RawSystem.heapManager.setElement(arrayObject, i, List.of(Word.of(label.getBytes()[i])));
    }

    var valueField =
        stringRawClass
            .findMemberFieldBy("value")
            .orElseThrow(() -> new RuntimeException("byte[] value field is not found"));
    RawSystem.heapManager.setValue(stringObject, valueField, List.of(Word.of(arrayObjectId)));

    // value =
    // coder = (byte)1

    return objectId;
  }

  private static final String STRING_CLASS_LABEL = "java/lang/String";

  public static final StringPool INSTANCE = new StringPoolSimulator();
}
