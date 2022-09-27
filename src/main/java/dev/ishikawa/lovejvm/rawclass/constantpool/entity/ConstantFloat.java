package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

/** ConstantFloat entry takes up TWO entries. */
public class ConstantFloat extends ConstantPoolUnresolvableEntry implements ConstantPoolEntry {
  private final float floatValue; // 4bytes

  public ConstantFloat(float value) {
    this.floatValue = value;
  }

  public float getFloatValue() {
    return floatValue;
  }

  public int getIntBits() {
    return Float.floatToIntBits(this.getFloatValue());
  }

  @Override
  public int size() {
    return 5;
  }
}
