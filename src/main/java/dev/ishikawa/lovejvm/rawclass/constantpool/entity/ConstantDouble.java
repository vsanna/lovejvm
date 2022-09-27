package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantDouble extends ConstantPoolUnresolvableEntry implements ConstantPoolEntry {
  private final double doubleValue; // 8bytes

  public ConstantDouble(double value) {
    this.doubleValue = value;
  }

  public double getDoubleValue() {
    return doubleValue;
  }

  @Override
  public int size() {
    return 9;
  }
}
