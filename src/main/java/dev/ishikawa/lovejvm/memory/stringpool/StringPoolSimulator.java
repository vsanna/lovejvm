package dev.ishikawa.lovejvm.memory.stringpool;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.RawSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StringPoolSimulator implements StringPool {
  public final Map<String, RawObject> stringMap = new HashMap();
  private RawClass stringRawClass;

  /**
   * create a String object,
   *
   * @return the newly created object's objectId
   */
  @Override
  public int register(String label) {
    if (stringRawClass == null) {
      stringRawClass = RawSystem.methodAreaManager.lookupOrLoadClass(STRING_CLASS_LABEL);
    }

    int objectId = RawSystem.heapManager.register(stringRawClass);
    RawObject rawObject = RawSystem.heapManager.lookupObject(objectId);
    stringMap.put(label, rawObject);
    return objectId;
  }

  /** @return objectId of the string object corresponding to the given label */
  @Override
  public int getOrCreate(String label) {
    return Optional.ofNullable(stringMap.get(label))
        .map(RawObject::getObjectId)
        .orElseGet(() -> register(label));
  }

  private static final String STRING_CLASS_LABEL = "java/lang/String";

  public static final StringPool INSTANCE = new StringPoolSimulator();
}
