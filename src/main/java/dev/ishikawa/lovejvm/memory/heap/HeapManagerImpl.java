package dev.ishikawa.lovejvm.memory.heap;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HeapManagerImpl implements HeapManager {
  // Map of objectId to ObjectEntry
  private Map<Integer, ObjectEntry> objectIdBasedObjectMap = new HashMap<Integer, ObjectEntry>();

  // Map of address to ObjectEntity
  private Map<Integer, ObjectEntry> addressBasedObjectMap = new HashMap<>();

  private int nextObjectId = 0;

  private final Heap heap = HeapSimulator.INSTANCE;

  private HeapManagerImpl() {}


  /**
   * allocate method doesn't consider freeing the used mem or cg at this moment.
   * it just moves ahead every single time a new object is created.
   *
   * @param baseClass
   */
  @Override
  public int register(RawClass baseClass) {
    byte[] bytes = new byte[baseClass.getObjectWords()];
    heap.allocate(bytes);

    int startingAddress = heap.headAddress();

    RawObject rawObject = createRawObject(startingAddress, baseClass);
    ObjectEntry objectEntry = new ObjectEntry(startingAddress, rawObject);

    addressBasedObjectMap.put(startingAddress, objectEntry);
    objectIdBasedObjectMap.put(rawObject.getObjectId(), objectEntry);

    return rawObject.getObjectId();
  }

  @Override
  public RawObject lookupObject(int objectId) {
    return Optional.ofNullable(objectIdBasedObjectMap.get(objectId))
        .map(it -> it.rawObject)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

  @Override
  public void setValue(int address, List<Word> value) {
    heap.setValue(address, value);
  }

  private RawObject createRawObject(int address, RawClass rawClass) {
    int objectId = nextObjectId;
    nextObjectId += 1;
    return new RawObject(objectId, address, rawClass);
  }

  static public final HeapManager INSTANCE = new HeapManagerImpl();

  private static class ObjectEntry {
    private int address;
    private RawObject rawObject;

    public ObjectEntry(int address, RawObject rawObject) {
      this.address = address;
      this.rawObject = rawObject;
    }

    public int getAddress() {
      return address;
    }

    public RawObject getRawObject() {
      return rawObject;
    }
  }
}
