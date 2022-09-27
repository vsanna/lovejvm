package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantUtf8 extends ConstantPoolUnresolvableEntry implements ConstantPoolEntry {
  private final String label;

  public ConstantUtf8(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public int size() {
    return 3 + label.getBytes().length;
  }
}
