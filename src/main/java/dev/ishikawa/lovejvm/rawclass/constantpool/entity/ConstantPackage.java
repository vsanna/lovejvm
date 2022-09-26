package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantPackage implements ConstantPoolEntry {
  private boolean isResolved = false;

  private int nameIndex;
  private ConstantUtf8 name;

  public ConstantPackage(int nameIndex) {
    this.nameIndex = nameIndex;
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
    return 3;
  }
}
