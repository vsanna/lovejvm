package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/**
 * ConstantMethodType points to a MethodType object. MethodType holds a type of returning value, and
 * list of types of its parameters this is a metadata of a method.
 */
public class ConstantMethodType extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private final int descriptorIndex; // 2bytes
  private ConstantUtf8 label;
  private int objectId; // to java.lang.invoke.MethodType

  public ConstantMethodType(int descriptorIndex) {
    this.descriptorIndex = descriptorIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    label = (ConstantUtf8) constantPool.findByIndex(descriptorIndex);
  }

  public ConstantUtf8 getLabel() {
    return label;
  }

  public int getObjectId() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return objectId;
  }

  public void setObjectId(int objectId) {
    this.objectId = objectId;
  }

  public int getDescriptorIndex() {
    return descriptorIndex;
  }

  public void setLabel(ConstantUtf8 label) {
    this.label = label;
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
