package dev.ishikawa.lovejvm.klass;

import java.util.List;

public class Interfaces {
    private final int entrySize;
    private List<LInterface> interfaces;

    public Interfaces(int entrySize, List<LInterface> entries) {
        if(entrySize != entries.size()) throw new RuntimeException("invalid Interfaces. the entrySize doesn't match with num of entries");
        this.entrySize = entrySize;
        this.interfaces = entries;
    }

    public int size() {
        return 2 + interfaces.stream().map(LInterface::size).reduce(0, Integer::sum);
    }
}
