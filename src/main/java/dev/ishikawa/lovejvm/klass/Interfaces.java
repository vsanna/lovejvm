package dev.ishikawa.lovejvm.klass;

import java.util.List;

public class Interfaces {
    private final int size;
    private List<LInterface> interfaces;

    public Interfaces(int size, List<LInterface> entries) {
        if(size != entries.size()) throw new RuntimeException("invalid Interfaces. the size doesn't match with num of entries");
        this.size = size;
        this.interfaces = entries;
    }

    public int getSize() {
        return size;
    }
}
