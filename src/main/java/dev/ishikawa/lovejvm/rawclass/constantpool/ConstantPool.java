package dev.ishikawa.lovejvm.rawclass.constantpool;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import java.util.List;

public class ConstantPool {
  private final int entrySize;
  private List<ConstantPoolEntry> entries;

  // entrySize = actual num of entries - 1
  // TODO: research why
  public ConstantPool(int entrySize, List<ConstantPoolEntry> entries) {
    if (entrySize - 1 != entries.size())
      throw new RuntimeException(
          "invalid constant pool. the entrySize doesn't match with num of entries");
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

  /** constant pool entity's index is 1-index. */
  public ConstantPoolEntry findByIndex(int index) {
    var entry = entries.get(index - 1);
    if (!entry.isResolved()) {
      entry.resolve(this);
    }
    return entry;
  }
}
