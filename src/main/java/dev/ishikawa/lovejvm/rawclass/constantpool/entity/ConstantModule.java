package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantModule extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int nameIndex; // 2bytes
  private ConstantUtf8 label;
  private int stringObjectId;

  public ConstantModule(int nameIndex) {
    this.nameIndex = nameIndex;
  }

  public ConstantUtf8 getLabel() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return label;
  }

  public int getStringObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return stringObjectId;
  }

  public int getNameIndex() {
    return nameIndex;
  }

  public void setLabel(ConstantUtf8 label) {
    this.label = label;
  }

  public void setStringObjectId(int stringObjectId) {
    this.stringObjectId = stringObjectId;
  }

  @Override
  public int size() {
    return 3;
  }
}
