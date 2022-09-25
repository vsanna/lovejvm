package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantString implements ConstantPoolEntry {
  private boolean isResolved = false;

  private int stringIndex; // 2bytes
  private ConstantUtf8 label;

  public ConstantString(int nameIndex) {
    this.stringIndex = nameIndex;
  }

  public ConstantUtf8 getLabel() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return label;
  }

  @Override
  public void resolve(ConstantPool constantPool) {
    this.label = (ConstantUtf8) constantPool.findByIndex(stringIndex);
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
