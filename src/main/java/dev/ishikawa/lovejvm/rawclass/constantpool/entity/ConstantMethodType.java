package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantMethodType extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int descriptorIndex; // 2bytes
  private ConstantUtf8 label;
  private int stringObjectId;

  public ConstantMethodType(int descriptorIndex) {
    this.descriptorIndex = descriptorIndex;
  }

  public ConstantUtf8 getLabel() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return label;
  }

  public int getStringObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return stringObjectId;
  }

  public void setStringObjectId(int stringObjectId) {
    this.stringObjectId = stringObjectId;
  }

  public int getDescriptorIndex() {
    return descriptorIndex;
  }

  public void setLabel(ConstantUtf8 label) {
    this.label = label;
  }

  @Override
  public int size() {
    return 3;
  }
}
