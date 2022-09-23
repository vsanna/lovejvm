package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantClass implements ConstantPoolEntry {
  private boolean isResolved = false;

  private int nameIndex; // 2bytes
  private ConstantUtf8 name;

  public ConstantClass(int nameIndex) {
    this.nameIndex = nameIndex;
  }

  public ConstantUtf8 getName() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return name;
  }

  @Override
  public void resolve(ConstantPool constantPool) {
    this.name = (ConstantUtf8) constantPool.findByIndex(nameIndex);
    isResolved = true;
  }

  @Override
  public boolean isResolved() {
    return isResolved;
  }

  @Override
  public int size() {
    return 3; // 3bytes
  }
}
