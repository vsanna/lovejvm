package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class ConstantClass extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private final int nameIndex; // 2bytes
  private ConstantUtf8 name;
  private int objectId; // reference to Class object

  public ConstantClass(int nameIndex) {
    this.nameIndex = nameIndex;
  }

  public int getNameIndex() {
    return nameIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    name = (ConstantUtf8) constantPool.findByIndex(nameIndex);
  }

  public ConstantUtf8 getName() {
    return name;
  }

  public void setName(ConstantUtf8 name) {
    this.name = name;
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
    return 3; // 3bytes
  }
}
