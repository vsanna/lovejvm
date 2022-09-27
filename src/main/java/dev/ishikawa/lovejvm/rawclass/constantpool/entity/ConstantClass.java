package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantClass extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int nameIndex; // 2bytes
  private ConstantUtf8 name;
  private int objectId; // reference to Class object

  public ConstantClass(int nameIndex) {
    this.nameIndex = nameIndex;
  }

  public int getNameIndex() {
    return nameIndex;
  }

  public ConstantUtf8 getName() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return name;
  }

  public void setName(ConstantUtf8 name) {
    this.name = name;
  }

  public int getObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return objectId;
  }

  public void setObjectId(int objectId) {
    this.objectId = objectId;
  }

  @Override
  public int size() {
    return 3; // 3bytes
  }
}
