package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantModule extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int nameIndex; // 2bytes
  private ConstantUtf8 label;
  private int stringObjectId;

  public ConstantModule(int nameIndex) {
    this.nameIndex = nameIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    label = (ConstantUtf8) constantPool.findByIndex(nameIndex);
  }

  public ConstantUtf8 getLabel() {
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
