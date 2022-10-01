package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/** ConstantLong entry takes up TWO entries. */
public class ConstantLong extends ConstantPoolUnresolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private final long longValue; // 8bytes

  public ConstantLong(long value) {
    this.longValue = value;
  }

  public long getLongValue() {
    return longValue;
  }

  @Override
  public List<Word> loadableValue() {
    return Word.of(longValue);
  }

  @Override
  public int size() {
    return 9;
  }
}
