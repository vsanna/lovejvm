package dev.ishikawa.lovejvm.rawobject;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

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
  @Nullable private final RawClass rawClass;

  // Element class
  @Nullable private final JvmType elementType;

  // the head address of this object located in heap area.
  private final int address;

  public RawObject(
      int objectId, int address, @Nullable RawClass rawClass, @Nullable JvmType elementType) {
    assert (Objects.isNull(rawClass) ^ Objects.isNull(elementType));

    this.objectId = objectId;
    this.address = address;
    this.rawClass = rawClass;
    this.elementType = elementType;
  }

  public int getObjectId() {
    return objectId;
  }

  public int getAddress() {
    return address;
  }

  public @Nullable RawClass getRawClass() {
    return rawClass;
  }

  public @Nullable JvmType getElementType() {
    return elementType;
  }
}
