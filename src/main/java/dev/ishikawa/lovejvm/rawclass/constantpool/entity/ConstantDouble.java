package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class ConstantDouble extends ConstantPoolUnresolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private final double doubleValue; // 8bytes

  public ConstantDouble(double value) {
    this.doubleValue = value;
  }

  public double getDoubleValue() {
    return doubleValue;
  }

  @Override
  public List<Word> loadableValue() {
    return Word.of(doubleValue);
  }

  @Override
  public int size() {
    return 9;
  }
}
