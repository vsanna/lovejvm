package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
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
  private int nextObjectId = 100;
  private final Map<Integer, ObjectEntry> objectIdBasedObjectMap = new HashMap<>();
  private final Map<Integer, ObjectEntry> addressBasedObjectMap = new HashMap<>();
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
  public int newObject(RawClass baseClass) {
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
  public int newArrayObject(RawArrayClass rawArrayClass, int arrSize) {
    return arrayclassObjectHandler.create(rawArrayClass, arrSize);
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

  // REFACTOR: ここじゃない. RawClass or ClassLoader
  @Override
  public RawObject createClassObject(RawClass targetClass) {
    RawClass classRawClass = RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Class");
    int objectId = RawSystem.heapManager.newObject(classRawClass);
    RawObject classObject = RawSystem.heapManager.lookupObject(objectId);

    // set classLoader field: null(cause this CL is bootstrap)
    RawField classLoaderField =
        classRawClass
            .findMemberFieldBy("classLoader")
            .orElseThrow(
                () -> new RuntimeException("classLoader field doesn't exist in Class.class"));
    RawSystem.heapManager.setValue(classObject, classLoaderField, Collections.emptyList());

    // set componentType field
    List<Word> componentTypeFieldValue;
    if (targetClass instanceof RawArrayClass) {
      componentTypeFieldValue =
          Optional.ofNullable(((RawArrayClass) targetClass).getComponentComplexClass())
              .map(RawClass::getClassObjectId)
              .map(it -> List.of(Word.of(it)))
              .orElseGet(Collections::emptyList);
    } else {
      componentTypeFieldValue = Collections.emptyList();
    }
    RawField componentType =
        classRawClass
            .findMemberFieldBy("componentType")
            .orElseThrow(
                () -> new RuntimeException("componentType field doesn't exist in Class.class"));
    RawSystem.heapManager.setValue(classObject, componentType, componentTypeFieldValue);

    return classObject;
  }

  private synchronized int requestNextObjectId() {
    int objectId = nextObjectId;
    nextObjectId += 1;
    return objectId;
  }

  private void addNewObjectEntry(int startingAddress, RawObject rawObject) {
    ObjectEntry objectEntry = new ObjectEntry(startingAddress, rawObject);
    addressBasedObjectMap.put(startingAddress, objectEntry);
    objectIdBasedObjectMap.put(rawObject.getObjectId(), objectEntry);
  }

  private Heap getHeap() {
    return heap;
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

              int size = object.getObjectWordSize() * Word.BYTES_SIZE;
              byte[] bytes = heap.retrieve(object.getAddress(), size);
              StringBuilder builder = new StringBuilder();
              for (byte aByte : bytes) {
                builder.append(String.format("%02X ", aByte));
              }

              System.out.printf(
                  "[HEAP DUMP]%5s|%5s|%20s(%5d)| %s\n",
                  object.getObjectId(),
                  object.getAddress(),
                  object
                      .getRawClass()
                      .getName()
                      .substring(0, Math.min(object.getRawClass().getName().length(), 18)),
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
      heapManager.addNewObjectEntry(startingAddress, rawObject);

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
      return new RawObject(objectId, address, rawClass, 0);
    }

    private int calcStartingAddress(RawObject rawObject, RawField rawField) {
      assert !(rawObject.getRawClass() instanceof RawArrayClass);
      int offsetToField = rawObject.getRawClass().offsetToMemberFieldBytes(rawField);
      return rawObject.getAddress() + offsetToField;
    }
  }

  private static class ArrayClassObjectHandler {
    private final HeapManagerImpl heapManager;

    public ArrayClassObjectHandler(HeapManagerImpl heapManager) {
      this.heapManager = heapManager;
    }

    public int create(RawArrayClass rawArrayClass, int arrSize) {
      int startingAddress = heapManager.heap.headAddress();

      // FIXME: it's ok to secure 32bits * arrSize when the element is int, but it should do 8 bits
      // * arrSize for bytes
      byte[] bytes = new byte[rawArrayClass.getComponentWordSize() * Word.BYTES_SIZE * arrSize];
      heapManager.heap.allocate(bytes);

      RawObject rawObject = createRawObject(startingAddress, rawArrayClass, arrSize);
      heapManager.addNewObjectEntry(startingAddress, rawObject);

      return rawObject.getObjectId();
    }

    public void setValue(RawObject rawObject, int position, List<Word> value) {
      int startingAddress = calcStartingAddress(rawObject, position);
      byte[] bytes = Word.toByteArray(value);
      heapManager.heap.save(startingAddress, bytes);
    }

    public List<Word> getValue(RawObject rawObject, int position) {
      int startingAddress = calcStartingAddress(rawObject, position);
      int elementBytesSize =
          ((RawArrayClass) rawObject.getRawClass()).getComponentWordSize() * Word.BYTES_SIZE;
      byte[] bytes = heapManager.heap.retrieve(startingAddress, elementBytesSize);
      return Word.of(bytes);
    }

    private RawObject createRawObject(int address, RawArrayClass rawArrayClass, int arrSize) {
      int objectId = heapManager.requestNextObjectId();
      return new RawObject(objectId, address, rawArrayClass, arrSize);
    }

    private int calcStartingAddress(RawObject rawObject, int position) {
      assert (rawObject.getRawClass() instanceof RawArrayClass);
      int objectAddress = rawObject.getAddress();
      int elementBytesSize =
          ((RawArrayClass) rawObject.getRawClass()).getComponentWordSize() * Word.BYTES_SIZE;
      int offsetToField = elementBytesSize * position;
      return objectAddress + offsetToField;
    }
  }

  private static class ObjectEntry {
    private final int address;
    private final RawObject rawObject;

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
