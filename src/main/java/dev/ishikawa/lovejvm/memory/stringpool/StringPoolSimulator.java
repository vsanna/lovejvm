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
import java.util.Map.Entry;
import java.util.Optional;

public class StringPoolSimulator implements StringPool {
  public final Map<String, RawObject> stringMap = new HashMap();
  private final RawSystem rawSystem;
  private RawClass stringRawClass;

  public StringPoolSimulator(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  /** @return objectId of the string object corresponding to the given label */
  @Override
  public int getOrCreate(String label) {
    return Optional.ofNullable(stringMap.get(label))
        .map(RawObject::getObjectId)
        .orElseGet(() -> register(label));
  }

  @Override
  public Optional<String> getLabelBy(int objectId) {
    return stringMap.entrySet().stream()
        .filter(it -> it.getValue().getObjectId() == objectId)
        .map(Entry::getKey)
        .findFirst();
  }

  @Override
  public int register(String label, RawObject stringRawObject) {
    stringMap.put(label, stringRawObject);
    return stringRawObject.getObjectId();
  }

  /**
   * create a new String object,
   *
   * @return the newly created object's objectId
   */
  private int register(String label) {
    if (stringRawClass == null) {
      stringRawClass = rawSystem.methodAreaManager().lookupOrLoadClass(STRING_CLASS_LABEL);
    }

    int objectId = rawSystem.heapManager().newObject(stringRawClass);
    RawObject stringObject = rawSystem.heapManager().lookupObject(objectId);
    stringMap.put(label, stringObject);

    // set byte array of the label into value field of this String object
    var byteArrayClass = RawArrayClass.lookupOrCreatePrimaryRawArrayClass(JvmType.BYTE, 1, rawSystem);
    var arrayObjectId = rawSystem.heapManager().newArrayObject(byteArrayClass, label.length());
    var arrayObject = rawSystem.heapManager().lookupObject(arrayObjectId);
    for (int i = 0; i < label.getBytes().length; i++) {
      // TODO: Now allocating 1 word for 1 byte. not efficient. fix it.
      rawSystem.heapManager().setElement(arrayObject, i, List.of(Word.of(label.getBytes()[i])));
    }

    var valueField =
        stringRawClass
            .findMemberFieldBy("value")
            .orElseThrow(() -> new RuntimeException("byte[] value field is not found"));
    rawSystem.heapManager().setValue(stringObject, valueField, List.of(Word.of(arrayObjectId)));

    return objectId;
  }

  public static final String STRING_CLASS_LABEL = "java/lang/String";
}
