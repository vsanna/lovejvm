package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantModule implements ConstantPoolEntry {
  private boolean isResolved = false;

  private int nameIndex; // 2bytes
  private ConstantUtf8 label;

  public ConstantModule(int nameIndex) {
    this.nameIndex = nameIndex;
  }

  public ConstantUtf8 getLabel() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return label;
  }

  @Override
  public void resolve(ConstantPool constantPool) {
    this.label = (ConstantUtf8) constantPool.findByIndex(nameIndex);
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
