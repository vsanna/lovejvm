package dev.ishikawa.lovejvm.rawobject;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;

/**
 * RawObject is a container of metadata of one object. It has info about how the object is located
 * in the heap area. - objectId(= ObjectRef) - starting address of the object stored in heap area
 * ... from where - the class the object belongs to ... how long
 *
 * <p>binary layout of objects - non array object: [4B: address of superclass object][size of
 * field]*(num of fields) - array object: [4B: address of superclass object][size of
 * element]*(length of the array)

 * RawObject is an object that contains a reference to bytearray[address:address+size-1].
 * We retrieve fields data via rawObject from the heap
 */
public class RawObject {
  private final int objectId;
  // the head address of this object located in heap area.
  private final int address;

  // RawClass returns the size of necessary binary
  private final RawClass rawClass;

  private final int arrSize;

  public RawObject(int objectId, int address, RawClass rawClass, int arrSize) {

    this.objectId = objectId;
    this.address = address;
    this.rawClass = rawClass;
    this.arrSize = arrSize;
  }

  public int getObjectId() {
    return objectId;
  }

  public int getAddress() {
    return address;
  }

  public int getArrSize() {
    return arrSize;
  }

  public RawClass getRawClass() {
    return rawClass;
  }

  public int getObjectWordSize() {
    if (getRawClass() instanceof RawArrayClass) {
      return ((RawArrayClass) getRawClass()).getComponentWordSize() * arrSize;
    } else {
      return getRawClass().getObjectWords();
    }
  }

  public static final int NULL = 0;
}
