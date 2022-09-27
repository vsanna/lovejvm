package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HeapManagerImpl implements HeapManager {
  // Map of objectId to ObjectEntry
  private Map<Integer, ObjectEntry> objectIdBasedObjectMap = new HashMap<Integer, ObjectEntry>();

  // Map of address to ObjectEntity
  private Map<Integer, ObjectEntry> addressBasedObjectMap = new HashMap<>();

  // objectId starts with 100. objectId = 0 means, it's null reference.
  private int nextObjectId = 100;

  private final Heap heap = HeapSimulator.INSTANCE;

  private HeapManagerImpl() {}

  /**
   * allocate method doesn't consider freeing the used mem or cg at this moment. it just moves ahead
   * every single time a new object is created.
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
  public void setValue(RawObject rawObject, RawField rawField, List<Word> value) {
    int objectAddress = objectIdBasedObjectMap.get(rawObject.getObjectId()).address;
    int offsetToField = rawObject.getRawClass().offsetToField(rawField);
    int startingAddress = objectAddress + offsetToField;
    byte[] bytes = Word.toByteArray(value);
    heap.save(startingAddress, bytes);
  }

  @Override
  public List<Word> getValue(RawObject rawObject, RawField rawField) {
    int objectAddress = objectIdBasedObjectMap.get(rawObject.getObjectId()).address;
    int offsetToField = rawObject.getRawClass().offsetToField(rawField);
    int startingAddress = objectAddress + offsetToField;

    int size = rawField.getJvmType().wordSize() * Word.BYTES_SIZE;
    return Word.of(heap.retrieve(startingAddress, size));
  }

  @Override
  public int registerArray(JvmType elementType, int arrSize) {
    byte[] bytes = new byte[elementType.wordSize() * Word.BYTES_SIZE * arrSize];
    heap.allocate(bytes);

    int startingAddress = heap.headAddress();

    RawObject rawObject = createRawArrayObject(startingAddress, elementType);
    ObjectEntry objectEntry = new ObjectEntry(startingAddress, rawObject);

    addressBasedObjectMap.put(startingAddress, objectEntry);
    objectIdBasedObjectMap.put(rawObject.getObjectId(), objectEntry);

    return rawObject.getObjectId();
  }

  @Override
  public void setElement(RawObject rawObject, int position, List<Word> value) {
    int objectAddress = objectIdBasedObjectMap.get(rawObject.getObjectId()).address;
    int offsetToField =
        (Objects.requireNonNull(rawObject.getElementType()).wordSize() * Word.BYTES_SIZE)
            * position;
    int startingAddress = objectAddress + offsetToField;
    byte[] bytes = Word.toByteArray(value);

    heap.save(startingAddress, bytes);
  }

  @Override
  public List<Word> getElement(RawObject rawObject, int position) {
    int objectAddress = objectIdBasedObjectMap.get(rawObject.getObjectId()).address;
    int elementBytesSize =
        Objects.requireNonNull(rawObject.getElementType()).wordSize() * Word.BYTES_SIZE;
    int offsetToField = elementBytesSize * position;
    int startingAddress = objectAddress + offsetToField;

    return Word.of(heap.retrieve(startingAddress, elementBytesSize));
  }

  @Override
  public RawObject lookupObject(int objectId) {
    return Optional.ofNullable(objectIdBasedObjectMap.get(objectId))
        .map(it -> it.rawObject)
        .orElseThrow(() -> new RuntimeException("Non existing object is tried to load"));
  }

  private RawObject createRawObject(int address, RawClass rawClass) {
    int objectId = nextObjectId;
    nextObjectId += 1;
    return new RawObject(objectId, address, rawClass, null);
  }

  private RawObject createRawArrayObject(int address, JvmType elementType) {
    int objectId = nextObjectId;
    nextObjectId += 1;
    return new RawObject(objectId, address, null, elementType);
  }

  public static final HeapManager INSTANCE = new HeapManagerImpl();

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
