package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantInteger extends ConstantPoolUnresolvableEntry implements ConstantPoolEntry {
  private final int intValue; // 4bytes

  public ConstantInteger(int value) {
    this.intValue = value;
  }

  public int getIntValue() {
    return intValue;
  }

  @Override
  public int size() {
    return 5;
  }
}
