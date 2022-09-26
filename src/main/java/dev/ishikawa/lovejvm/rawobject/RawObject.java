package dev.ishikawa.lovejvm.rawobject;


import dev.ishikawa.lovejvm.rawclass.RawClass;

/**
 * RawObject is a container of metadata of one object. It has info about how the object is located
 * in the heap area. - objectId(= ObjectRef) - starting address of the object stored in heap area
 * ... from where - the class the object belongs to ... how long
 *
 * <p>binary layout of objects - non array object: [4B: address of superclass object][size of
 * field]*(num of fields) - array object: [4B: address of superclass object][size of
 * element]*(length of the array)
 */
public class RawObject {
  private final int objectId;

  // RawClass returns the size of necessary binary
  private final RawClass rawClass;

  // the head address of this object located in heap area.
  private final int address;

  public RawObject(int objectId, int address, RawClass rawClass) {
    this.objectId = objectId;
    this.address = address;
    this.rawClass = rawClass;
  }

  public int getObjectId() {
    return objectId;
  }

  public int getAddress() {
    return address;
  }

  public RawClass getRawClass() {
    return rawClass;
  }
}
