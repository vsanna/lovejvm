package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantInvokeDynamic extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry {
  private final int bootstrapMethodAttrIndex; // 2bytes

  private final int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType;
  private int methodTypeObjectId;

  private int callSiteObjectId; // to java.lang.invoke.CallSite

  private ConstantMethodref methodRef;

  public ConstantInvokeDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
    this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    nameAndType = (ConstantNameAndType) constantPool.findByIndex(nameAndTypeIndex);
  }

  public ConstantNameAndType getNameAndType() {
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

  public int getCallSiteObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return callSiteObjectId;
  }

  public void setCallSiteObjectId(int callSiteObjectId) {
    this.callSiteObjectId = callSiteObjectId;
  }

  @Override
  public int size() {
    return 5;
  }

  public int getMethodTypeObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return methodTypeObjectId;
  }

  public void setMethodTypeObjectId(int methodTypeObjectId) {
    this.methodTypeObjectId = methodTypeObjectId;
  }

  public ConstantMethodref getMethodRef() {
    return methodRef;
  }

  public void setMethodRef(ConstantMethodref methodRef) {
    this.methodRef = methodRef;
  }
}
