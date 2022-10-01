package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class ConstantMethodHandle extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private boolean isResolved = false;

  private final int referenceKind; // 1byte

  private final int referenceIndex; // 2byte
  private ConstantPoolEntry reference;
  private int objectId; // to java.lang.invoke.MethodHandle

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

  @Override
  public void shakeOut(ConstantPool constantPool) {
    reference = constantPool.findByIndex(referenceIndex);
  }

  public ConstantPoolEntry getReference() {
    return reference;
  }

  public int getReferenceKind() {
    return referenceKind;
  }

  public int getReferenceIndex() {
    return referenceIndex;
  }

  public void setReference(ConstantPoolEntry reference) {
    this.reference = reference;
  }

  public int getObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return objectId;
  }

  public void setObjectId(int objectId) {
    this.objectId = objectId;
  }

  @Override
  public List<Word> loadableValue() {
    return List.of(Word.of(getObjectId()));
  }

  @Override
  public int size() {
    return 4;
  }
}
