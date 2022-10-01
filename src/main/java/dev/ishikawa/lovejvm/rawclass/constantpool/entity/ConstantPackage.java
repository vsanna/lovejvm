package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantPackage extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int nameIndex;
  private ConstantUtf8 name;
  private int stringObjectId;

  public ConstantPackage(int nameIndex) {
    this.nameIndex = nameIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    name = (ConstantUtf8) constantPool.findByIndex(nameIndex);
  }

  public int getNameIndex() {
    return nameIndex;
  }

  public ConstantUtf8 getName() {
    return name;
  }

  public void setName(ConstantUtf8 name) {
    this.name = name;
  }

  public int getStringObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return stringObjectId;
  }

  public void setStringObjectId(int stringObjectId) {
    this.stringObjectId = stringObjectId;
  }

  @Override
  public int size() {
    return 3;
  }
}
