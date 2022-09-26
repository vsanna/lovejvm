package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantInvokeDynamic implements ConstantPoolEntry {
  private boolean isResolved = false;

  private int bootstrapMethodAttrIndex; // 2bytes

  private int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType;

  public ConstantInvokeDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
    this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  public ConstantNameAndType getNameAndType() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return nameAndType;
  }

  @Override
  public void resolve(ConstantPool constantPool) {
    this.nameAndType = (ConstantNameAndType) constantPool.findByIndex(nameAndTypeIndex);
    isResolved = true;
  }

  @Override
  public boolean isResolved() {
    return isResolved;
  }

  @Override
  public int size() {
    return 5;
  }
}
