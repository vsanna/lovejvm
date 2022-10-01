package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class ConstantString extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private final int stringIndex; // 2bytes
  private ConstantUtf8 label;
  private int objectId;

  public ConstantString(int nameIndex) {
    this.stringIndex = nameIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    label = (ConstantUtf8) constantPool.findByIndex(stringIndex);
  }

  public int getStringIndex() {
    return stringIndex;
  }

  public ConstantUtf8 getLabel() {
    return label;
  }

  public void setLabel(ConstantUtf8 label) {
    this.label = label;
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
    return 3;
  }
}
