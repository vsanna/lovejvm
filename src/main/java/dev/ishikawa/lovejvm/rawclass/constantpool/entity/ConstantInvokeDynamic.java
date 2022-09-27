package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

public class ConstantInvokeDynamic extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry {
  private final int bootstrapMethodAttrIndex; // 2bytes

  private final int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType;
  // resolve to what?

  public ConstantInvokeDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
    this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  public ConstantNameAndType getNameAndType() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return nameAndType;
  }

  public int getBootstrapMethodAttrIndex() {
    return bootstrapMethodAttrIndex;
  }

  public int getNameAndTypeIndex() {
    return nameAndTypeIndex;
  }

  public void setNameAndType(ConstantNameAndType nameAndType) {
    this.nameAndType = nameAndType;
  }

  @Override
  public int size() {
    return 5;
  }
}
