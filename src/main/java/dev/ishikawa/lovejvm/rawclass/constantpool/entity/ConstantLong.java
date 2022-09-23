package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantLong implements ConstantPoolEntry {
  private long longValue; // 8bytes

  public ConstantLong(long value) {
    this.longValue = value;
  }

  public long getLongValue() {
    return longValue;
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
    return 9;
  }
}
