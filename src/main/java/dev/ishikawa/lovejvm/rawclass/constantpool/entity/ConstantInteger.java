package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class ConstantInteger extends ConstantPoolUnresolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private final int intValue; // 4bytes

  public ConstantInteger(int value) {
    this.intValue = value;
  }

  public int getIntValue() {
    return intValue;
  }

  @Override
  public List<Word> loadableValue() {
    return List.of(Word.of(intValue));
  }

  @Override
  public int size() {
    return 5;
  }
}
