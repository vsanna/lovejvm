package dev.ishikawa.lovejvm.rawclass.constantpool;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import java.util.List;

public class ConstantPool {
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

  public ConstantPoolEntry findByIndex(int index) {
    var entry = entries.get(index);
    if (!entry.isResolved()) {
      entry.resolve(this);
    }
    return entry;
  }
}
