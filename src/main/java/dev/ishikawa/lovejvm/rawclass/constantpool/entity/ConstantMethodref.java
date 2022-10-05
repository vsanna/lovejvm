package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;

/**
 * ConstantMethodref is a tuple of {className, name, descriptor}
 * ConstantMethodref itself is the key to identify what method to use.
 *
 * This is not loadable, so no extra value is put when resolving this entry.
 *
 * While resolving this entry, related other entries will be also resolved(ConstantClass)
 * */
public class ConstantMethodref extends ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private final int classIndex; // 2bytes
  private ConstantClass constantClassRef;
  private int classObjectId; // TODO: maybe not needed

  private final int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType; // TODO: maybe not needed

  // as a cache to avoid resolving ConstaMethodref/ConstInterfaceMethodref everytime
  private RawMethod rawMethod;

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

  public RawMethod getRawMethod() {
    return rawMethod;
  }

  public void setRawMethod(RawMethod rawMethod) {
    this.rawMethod = rawMethod;
  }

  @Override
  public int size() {
    return 5;
  }
}
