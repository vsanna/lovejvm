package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantDynamic extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int bootstrapMethodAttrIndex; // 2bytes

  private final int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType;
  // resolve to what?

  public ConstantDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
    this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  public int getNameAndTypeIndex() {
    return nameAndTypeIndex;
  }

  public ConstantNameAndType getNameAndType() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return nameAndType;
  }

  public void setNameAndType(ConstantNameAndType nameAndType) {
    this.nameAndType = nameAndType;
  }

  public int getBootstrapMethodAttrIndex() {
    return bootstrapMethodAttrIndex;
  }

  @Override
  public int size() {
    return 5;
  }
}
