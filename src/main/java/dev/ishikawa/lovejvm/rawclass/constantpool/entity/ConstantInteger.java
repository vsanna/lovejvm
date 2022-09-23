package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantInteger implements ConstantPoolEntry {
  private int intValue; // 4bytes

  public ConstantInteger(int value) {
    this.intValue = value;
  }

  public int getIntValue() {
    return intValue;
  }

  @Override
  public void resolve(ConstantPool constantPool) {
    // noop
  }

  @Override
  public boolean isResolved() {
    return true;
  }

  @Override
  public int size() {
    return 5;
  }
}
