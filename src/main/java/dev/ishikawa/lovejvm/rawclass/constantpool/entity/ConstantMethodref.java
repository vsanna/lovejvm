package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantMethodref extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int classIndex; // 2bytes
  private ConstantClass constantClassRef;
  private int classObjectId;

  private final int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType;
  private int methodObjectId;

  public ConstantMethodref(int classIndex, int nameAndTypeIndex) {
    this.classIndex = classIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    constantClassRef = (ConstantClass) constantPool.findByIndex(classIndex);
    nameAndType = (ConstantNameAndType) constantPool.findByIndex(nameAndTypeIndex);
  }

  public ConstantNameAndType getNameAndType() {
    return nameAndType;
  }

  public ConstantClass getConstantClassRef() {
    return constantClassRef;
  }

  public int getClassObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return classObjectId;
  }

  public int getMethodObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return methodObjectId;
  }

  public int getClassIndex() {
    return classIndex;
  }

  public int getNameAndTypeIndex() {
    return nameAndTypeIndex;
  }

  public void setConstantClassRef(ConstantClass constantClassRef) {
    this.constantClassRef = constantClassRef;
  }

  public void setClassObjectId(int classObjectId) {
    this.classObjectId = classObjectId;
  }

  public void setNameAndType(ConstantNameAndType nameAndType) {
    this.nameAndType = nameAndType;
  }

  public void setMethodObjectId(int methodObjectId) {
    this.methodObjectId = methodObjectId;
  }

  @Override
  public int size() {
    return 5;
  }
}
