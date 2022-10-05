package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/**
 * CONSTANT_Dynamic_info
 * While resolving this entry, JVM computes its const value dynamically.
 * To compute the value, JVM invokes "bootstrap method.
 * NameAndType express this value's field name and type.
 *
 * bootstrapMethodAttrIndex specifies the item in THIS CLASS's bootstrap method.
 */
public class ConstantDynamic extends ConstantPoolResolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private final int bootstrapMethodAttrIndex; // 2bytes

  private final int nameAndTypeIndex; // 2byte
  private ConstantNameAndType nameAndType;

  private List<Word> dynamicValue;

  public ConstantDynamic(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
    this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }

  @Override
  public void shakeOut(ConstantPool constantPool) {
    nameAndType = (ConstantNameAndType) constantPool.findByIndex(nameAndTypeIndex);
  }

  public int getNameAndTypeIndex() {
    return nameAndTypeIndex;
  }

  public ConstantNameAndType getNameAndType() {
    return nameAndType;
  }

  public void setNameAndType(ConstantNameAndType nameAndType) {
    this.nameAndType = nameAndType;
  }

  public int getBootstrapMethodAttrIndex() {
    return bootstrapMethodAttrIndex;
  }

  public List<Word> getDynamicValue() {
    if (!isResolved()) throw new RuntimeException("not resolved yet");
    return dynamicValue;
  }

  public void setDynamicValue(List<Word> dynamicValue) {
    this.dynamicValue = dynamicValue;
  }

  @Override
  public List<Word> loadableValue() {
    return getDynamicValue();
  }

  @Override
  public int size() {
    return 5;
  }
}
