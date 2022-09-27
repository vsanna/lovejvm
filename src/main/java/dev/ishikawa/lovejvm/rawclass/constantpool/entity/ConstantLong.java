package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

/** ConstantLong entry takes up TWO entries. */
public class ConstantLong extends ConstantPoolUnresolvableEntry implements ConstantPoolEntry {
  private final long longValue; // 8bytes

  public ConstantLong(long value) {
    this.longValue = value;
  }

  public long getLongValue() {
    return longValue;
  }

  @Override
  public int size() {
    return 9;
  }
}
