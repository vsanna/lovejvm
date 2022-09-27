package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantString extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private int stringIndex; // 2bytes
  private ConstantUtf8 label;
  private int objectId;

  public ConstantString(int nameIndex) {
    this.stringIndex = nameIndex;
  }

  public int getStringIndex() {
    return stringIndex;
  }

  public ConstantUtf8 getLabel() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return label;
  }

  public void setLabel(ConstantUtf8 label) {
    this.label = label;
  }

  public int getObjectId() {
    return objectId;
  }

  public void setObjectId(int objectId) {
    this.objectId = objectId;
  }

  @Override
  public int size() {
    return 3;
  }
}
