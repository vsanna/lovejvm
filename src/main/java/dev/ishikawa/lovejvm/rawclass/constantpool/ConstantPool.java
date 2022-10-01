package dev.ishikawa.lovejvm.rawclass.constantpool;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import java.util.List;

public class ConstantPool {
  private RawClass rawClass;
  private final int entrySize;
  private List<ConstantPoolEntry> entries;

  public ConstantPool(int entrySize, List<ConstantPoolEntry> entries) {
    if (entrySize != entries.size()) {
      throw new RuntimeException(
          "invalid constant pool. the entrySize doesn't match with num of entries");
    }
    this.entrySize = entrySize;
    this.entries = entries;
  }

  public int getEntrySize() {
    return entrySize;
  }

  /** @return length of bytes for the constant pool */
  public int size() {
    return 2 + entries.stream().map(ConstantPoolEntry::size).reduce(0, Integer::sum);
  }

  public void setRawClass(RawClass rawClass) {
    this.rawClass = rawClass;
  }

  public RawClass getRawClass() {
    return rawClass;
  }

  public ConstantPoolEntry findByIndex(int index) {
    return entries.get(index);
  }

  public void shakeOut() {
    entries.forEach(it -> it.shakeOut(this));
  }
}
