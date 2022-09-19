package dev.ishikawa.lovejvm.klass.constantpool;

import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantPoolEntry;

import java.util.List;

public class ConstantPool {
    private final int size;
    private List<ConstantPoolEntry> entries;

    // entrySize = actual num of entries - 1
    // TODO: research why
    public ConstantPool(int size, List<ConstantPoolEntry> entries) {
        if(size -1 != entries.size()) throw new RuntimeException("invalid constant pool. the size doesn't match with num of entries");
        this.size = size;
        this.entries = entries;
    }

    public int getSize() {
        return size;
    }

    /**
     * constant pool entity's index is 1-index.
     * */
    public ConstantPoolEntry findByIndex(int index) {
        var entry = entries.get(index - 1);
        if(!entry.isResolved()) {
            entry.resolve(this);
        }
        return entry;
    }
}
