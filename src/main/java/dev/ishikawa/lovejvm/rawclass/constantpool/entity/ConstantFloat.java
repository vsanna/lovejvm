package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/** ConstantFloat entry takes up TWO entries. */
public class ConstantFloat extends ConstantPoolUnresolvableEntry
    implements ConstantPoolEntry, ConstantPoolLoadableEntry {
  private final float floatValue; // 4bytes

  public ConstantFloat(float value) {
    this.floatValue = value;
  }

  public float getFloatValue() {
    return floatValue;
  }

  public int getIntBits() {
    return Float.floatToIntBits(this.getFloatValue());
  }

  @Override
  public List<Word> loadableValue() {
    return List.of(Word.of(floatValue));
  }

  @Override
  public int size() {
    return 5;
  }
}
