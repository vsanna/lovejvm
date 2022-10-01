package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantFieldref extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int classIndex; // 2bytes
  private ConstantClass constantClassRef;
  private int classObjectId;

  private final int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType;
  private int fieldObjectId;

  public ConstantFieldref(int classIndex, int nameAndTypeIndex) {
    this.classIndex = classIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    nameAndType = (ConstantNameAndType) constantPool.findByIndex(nameAndTypeIndex);
    constantClassRef = (ConstantClass) constantPool.findByIndex(classIndex);
  }

  public ConstantNameAndType getNameAndType() {
    return nameAndType;
  }

  public int getClassIndex() {
    return classIndex;
  }

  public void setConstantClassRef(ConstantClass constantClassRef) {
    this.constantClassRef = constantClassRef;
  }

  public ConstantClass getConstantClassRef() {
    return constantClassRef;
  }

  public int getClassObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return classObjectId;
  }

  public void setClassObjectId(int classObjectId) {
    this.classObjectId = classObjectId;
  }

  public int getNameAndTypeIndex() {
    return nameAndTypeIndex;
  }

  public void setNameAndType(ConstantNameAndType nameAndType) {
    this.nameAndType = nameAndType;
  }

  public int getFieldObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return fieldObjectId;
  }

  public void setFieldObjectId(int fieldObjectId) {
    this.fieldObjectId = fieldObjectId;
  }

  @Override
  public int size() {
    return 5;
  }
}
