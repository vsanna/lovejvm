package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantMethodHandle implements ConstantPoolEntry {
  private boolean isResolved = false;

  private final int referenceKind; // 1byte

  private final int referenceIndex; // 2byte
  private ConstantPoolEntry reference;

  public ConstantMethodHandle(int referenceKind, int referenceIndex) {
    assert (referenceKind >= 1 && referenceKind <= 9);
    this.referenceKind = referenceKind;
    this.referenceIndex = referenceIndex;
    // TODO: follow this rule
    // when 1 (REF_getField), 2 (REF_getStatic), 3 (REF_putField), or 4 (REF_putStatic)
    //   then CONSTANT_Fieldref_info
    // when 5 (REF_invokeVirtual), 6 (REF_invokeStatic), 7 (REF_invokeSpecial), or 8
    // (REF_newInvokeSpecial),
    //   then CONSTANT_Methodref_info
    // when 9 (REF_invokeInterface)
    //   then CONSTANT_InterfaceMethodref_info
    // AND
    // when 5 (REF_invokeVirtual), 6 (REF_invokeStatic), 7 (REF_invokeSpecial), or 9
    // (REF_invokeInterface),
    //   then method name must not be <init> or <clinit>.
    // when 8 (REF_newInvokeSpecial)
    //   then method name must be <init>
  }

  public ConstantPoolEntry getReference() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return reference;
  }

  @Override
  public void resolve(ConstantPool constantPool) {
    //    this.constantClassRef = (ConstantClass) constantPool.findByIndex(referenceKind);
    //    this.nameAndType = (ConstantNameAndType) constantPool.findByIndex(referenceIndex);
    this.reference = constantPool.findByIndex(referenceIndex);
    isResolved = true;
  }

  @Override
  public boolean isResolved() {
    return isResolved;
  }

  @Override
  public int size() {
    return 4;
  }
}
