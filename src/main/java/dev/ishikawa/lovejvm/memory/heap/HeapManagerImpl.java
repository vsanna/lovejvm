package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class HeapManagerImpl implements HeapManager {
  // Map of objectId to ObjectEntry
  private final Map<Integer, ObjectEntry> objectIdBasedObjectMap =
      new HashMap<Integer, ObjectEntry>();
  // Map of address to ObjectEntity
  private final Map<Integer, ObjectEntry> addressBasedObjectMap = new HashMap<>();
  // objectId starts with 100. objectId = 0 means, it's null reference.
  private int nextObjectId = 100;

  private final ClassObjectHandler classObjectHandler = new ClassObjectHandler(this);
  private final ArrayClassObjectHandler arrayclassObjectHandler = new ArrayClassObjectHandler(this);

  private final Heap heap = HeapSimulator.INSTANCE;

  public static final HeapManager INSTANCE = new HeapManagerImpl();

  private HeapManagerImpl() {}

  /**
   * allocate method doesn't consider freeing the used mem or cg at this moment. it just moves ahead
   * every single time a new object is created.
   *
   * @param baseClass
   */
  @Override
  public int register(RawClass baseClass) {
    return classObjectHandler.create(baseClass);
  }

  @Override
  public void setValue(RawObject rawObject, RawField rawField, List<Word> value) {
    classObjectHandler.setValue(rawObject, rawField, value);
  }

  @Override
  public List<Word> getValue(RawObject rawObject, RawField rawField) {
    return classObjectHandler.getValue(rawObject, rawField);
  }

  @Override
  public int registerArray(JvmType elementType, int arrSize) {
    return arrayclassObjectHandler.create(elementType, arrSize);
  }

  @Override
  public void setElement(RawObject rawObject, int position, List<Word> value) {
    arrayclassObjectHandler.setValue(rawObject, position, value);
  }

  @Override
  public List<Word> getElement(RawObject rawObject, int position) {
    return arrayclassObjectHandler.getValue(rawObject, position);
  }

  @Override
  public RawObject lookupObject(int objectId) {
    return Optional.ofNullable(objectIdBasedObjectMap.get(objectId))
        .map(it -> it.rawObject)
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Non existing object is tried to load. %s", objectId)));
  }

  @Override
  public RawObject createClassObject(RawClass targetClass) {
    RawClass classRawClass = RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Class");
    int objectId = RawSystem.heapManager.register(classRawClass);
    RawObject classObject = RawSystem.heapManager.lookupObject(objectId);

    // NOTE/TODO: Class classだけはinitialize前にobjectの生成を許す? 許せそう.

    // set classLoader field: null(cause this CL is bootstrap)
    RawField classLoaderField =
        classRawClass
            .findMemberFieldBy("classLoader")
            .orElseThrow(() -> new RuntimeException("classLoaded doesn't exist in Class.class"));
    RawSystem.heapManager.setValue(classObject, classLoaderField, Collections.emptyList());

    List<Word> componentTypeFieldValue;
    if (targetClass instanceof RawArrayClass) {
      // TODO: set arrayComponentType:
      componentTypeFieldValue = Collections.emptyList();
    } else {
      componentTypeFieldValue = Collections.emptyList();
    }
    RawField componentType =
        classRawClass
            .findMemberFieldBy("componentType")
            .orElseThrow(() -> new RuntimeException("componentType; doesn't exist in Class.class"));
    RawSystem.heapManager.setValue(classObject, componentType, componentTypeFieldValue);
    return classObject;
  }

  private synchronized int requestNextObjectId() {
    int objectId = nextObjectId;
    nextObjectId += 1;
    return objectId;
  }

  private void setNewObject(int startingAddress, RawObject rawObject) {
    ObjectEntry objectEntry = new ObjectEntry(startingAddress, rawObject);
    addressBasedObjectMap.put(startingAddress, objectEntry);
    objectIdBasedObjectMap.put(rawObject.getObjectId(), objectEntry);
  }

  private Heap getHeap() {
    return heap;
  }

  private int getAddress(RawObject rawObject) {
    return objectIdBasedObjectMap.get(rawObject.getObjectId()).address;
  }

  @Override
  public void dump() {
    System.out.println("[HEAP DUMP] objId| addr| className(osize)|bytes");

    addressBasedObjectMap.entrySet().stream()
        .sorted(Comparator.comparingInt(Entry::getKey))
        .forEach(
            (record) -> {
              ObjectEntry entry = record.getValue();

              // objectId|address|RawClass name(size)|(size)bytes
              RawObject object = entry.getRawObject();
              int size =
                  Optional.ofNullable(object.getRawClass())
                      .map(RawClass::getObjectWords)
                      .or(
                          () ->
                              Optional.of(object.getElementType().wordSize())
                                  .map(it -> it * object.getArrSize()))
                      .map(it -> it * Word.BYTES_SIZE)
                      .orElse(0);
              byte[] bytes = heap.retrieve(object.getAddress(), size);
              StringBuilder builder = new StringBuilder();
              for (byte aByte : bytes) {
                builder.append(String.format("%02X ", aByte));
              }

              System.out.printf(
                  "[HEAP DUMP]%5s|%5s|%20s(%5d)| %s\n",
                  object.getObjectId(),
                  object.getAddress(),
                  Optional.ofNullable(object.getRawClass())
                      .map(RawClass::getName)
                      .map(it -> it.substring(0, Math.min(it.length(), 18)))
                      .or(
                          () ->
                              Optional.ofNullable(object.getElementType())
                                  .map(JvmType::getJvmSignature)
                                  .map(it -> it + "[]"))
                      .orElse("N/A"),
                  size,
                  builder);
            });
  }

  private static class ClassObjectHandler {
    private final HeapManagerImpl heapManager;

    public ClassObjectHandler(HeapManagerImpl heapManager) {
      this.heapManager = heapManager;
    }

    public int create(RawClass baseClass) {
      int startingAddress = heapManager.getHeap().headAddress();

      byte[] bytes = new byte[baseClass.getObjectWords() * Word.BYTES_SIZE];
      heapManager.getHeap().allocate(bytes);

      RawObject rawObject = createRawObject(startingAddress, baseClass);
      heapManager.setNewObject(startingAddress, rawObject);

      return rawObject.getObjectId();
    }

    public void setValue(RawObject rawObject, RawField rawField, List<Word> value) {
      int startingAddress = calcStartingAddress(rawObject, rawField);
      byte[] bytes = Word.toByteArray(value);
      heapManager.heap.save(startingAddress, bytes);
    }

    public List<Word> getValue(RawObject rawObject, RawField rawField) {
      int startingAddress = calcStartingAddress(rawObject, rawField);
      int size = rawField.getJvmType().wordSize() * Word.BYTES_SIZE;
      byte[] bytes = heapManager.heap.retrieve(startingAddress, size);
      return Word.of(bytes);
    }

    private RawObject createRawObject(int address, RawClass rawClass) {
      int objectId = heapManager.requestNextObjectId();
      return new RawObject(objectId, address, rawClass, null, 0);
    }

    private int calcStartingAddress(RawObject rawObject, RawField rawField) {
      assert !(rawObject.getRawClass() instanceof RawArrayClass);
      int objectAddress = heapManager.getAddress(rawObject);
      int offsetToField = rawObject.getRawClass().offsetToMemberFieldBytes(rawField);
      return objectAddress + offsetToField;
    }
  }

  private static class ArrayClassObjectHandler {
    private final HeapManagerImpl heapManager;

    public ArrayClassObjectHandler(HeapManagerImpl heapManager) {
      this.heapManager = heapManager;
    }

    public int create(JvmType elementType, int arrSize) {
      int startingAddress = heapManager.heap.headAddress();
      // TODO: ここは間違っている. intのarrなら32bit * arrSize分確保してよいが、byteなら8bit * arrSize分になるべき
      byte[] bytes = new byte[elementType.wordSize() * Word.BYTES_SIZE * arrSize];
      heapManager.heap.allocate(bytes);

      RawObject rawObject = createRawObject(startingAddress, elementType, arrSize);
      heapManager.setNewObject(startingAddress, rawObject);

      return rawObject.getObjectId();
    }

    public void setValue(RawObject rawObject, int position, List<Word> value) {
      int startingAddress = calcStartingAddress(rawObject, position);
      byte[] bytes = Word.toByteArray(value);
      heapManager.heap.save(startingAddress, bytes);
    }

    public List<Word> getValue(RawObject rawObject, int position) {
      int startingAddress = calcStartingAddress(rawObject, position);
      int elementBytesSize = rawObject.getElementType().wordSize() * Word.BYTES_SIZE;
      byte[] bytes = heapManager.heap.retrieve(startingAddress, elementBytesSize);
      return Word.of(bytes);
    }

    private RawObject createRawObject(int address, JvmType elementType, int arrSize) {
      int objectId = heapManager.requestNextObjectId();
      // TODO: create ArrayClass
      return new RawObject(objectId, address, null, elementType, arrSize);
    }

    private int calcStartingAddress(RawObject rawObject, int position) {
      assert (rawObject.getRawClass() instanceof RawArrayClass);
      int objectAddress = heapManager.getAddress(rawObject);
      int elementBytesSize = rawObject.getElementType().wordSize() * Word.BYTES_SIZE;
      int offsetToField = elementBytesSize * position;
      return objectAddress + offsetToField;
    }
  }

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
