package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConstantMethodHandle extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {

  private final DescriptionKind descriptionKind;

  private final int referenceIndex; // 2byte
  private ConstantPoolEntry reference;
  private int objectId; // to java.lang.invoke.MethodHandle

  public ConstantMethodHandle(int referenceKind, int referenceIndex) {
    assert (referenceKind >= 1 && referenceKind <= 9);
    // 1byte
    this.referenceIndex = referenceIndex;
    this.descriptionKind = DescriptionKind.findByKind(referenceKind);
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    reference = constantPool.findByIndex(referenceIndex);
  }

  public ConstantPoolEntry getReference() {
    return reference;
  }

  public DescriptionKind getDescriptionKind() {
    return descriptionKind;
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

  public enum DescriptionKind {
    REF_getField(1, "(C)T"),
    REF_getStatic(2, "()T"),
    REF_putField(3, "(C,T)V"),
    REF_putStatic(4, "(T)V"),
    REF_invokeVirtual(5, "(C,A*)T"),
    REF_invokeStatic(6, "(A*)T"),
    REF_invokeSpecial(7, "(C,A*)T"),
    REF_newInvokeSpecial(8, "(A*)C"),
    REF_invokeInterface(9, "(C,A*)T");

    private final int kind;
    private final String methodDescriptor;

    DescriptionKind(int kind, String methodDescriptor) {
      this.kind = kind;
      this.methodDescriptor = methodDescriptor;
    }

    public String getMethodDescriptor() {
      return methodDescriptor;
    }

    public static final Map<Integer, DescriptionKind> map =
        Arrays.stream(DescriptionKind.values())
            .collect(Collectors.toMap((value) -> value.kind, (value) -> value));

    public static DescriptionKind findByKind(int kind) {
      return Optional.ofNullable(map.get(kind))
          .orElseThrow(() -> new IllegalArgumentException("invalid kind is passed"));
    }
  }
}
