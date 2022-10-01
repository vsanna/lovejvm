package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantNameAndType extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int nameIndex; // 2bytes
  private ConstantUtf8 name;
  private int nameStringObjectId;

  private final int descriptorIndex; // 2bytes
  private ConstantUtf8 descriptor;
  private int descriptorStringObjectId;

  public ConstantNameAndType(int nameIndex, int descriptorIndex) {
    this.nameIndex = nameIndex;
    this.descriptorIndex = descriptorIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    name = (ConstantUtf8) constantPool.findByIndex(nameIndex);
    descriptor = (ConstantUtf8) constantPool.findByIndex(descriptorIndex);
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

  public int getDescriptorIndex() {
    return descriptorIndex;
  }

  public ConstantUtf8 getDescriptor() {
    return descriptor;
  }

  public int getNameStringObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return nameStringObjectId;
  }

  public void setNameStringObjectId(int nameStringObjectId) {
    this.nameStringObjectId = nameStringObjectId;
  }

  public void setDescriptor(ConstantUtf8 descriptor) {
    this.descriptor = descriptor;
  }

  public int getDescriptorStringObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return descriptorStringObjectId;
  }

  public void setDescriptorStringObjectId(int descriptorStringObjectId) {
    this.descriptorStringObjectId = descriptorStringObjectId;
  }

  @Override
  public int size() {
    return 5;
  }
}
