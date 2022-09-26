package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantMethodType implements ConstantPoolEntry {
  private boolean isResolved = false;

  private int descriptorIndex; // 2bytes
  private ConstantUtf8 label;

  public ConstantMethodType(int descriptorIndex) {
    this.descriptorIndex = descriptorIndex;
  }

  public ConstantUtf8 getLabel() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return label;
  }

  @Override
  public void resolve(ConstantPool constantPool) {
    this.label = (ConstantUtf8) constantPool.findByIndex(descriptorIndex);
    isResolved = true;
  }

  @Override
  public boolean isResolved() {
    return isResolved;
  }

  @Override
  public int size() {
    return 3;
  }
}
