package dev.ishikawa.lovejvm.klass;

import java.util.List;

public class Fields {
    private final int entrySize;
    private List<LField> fields;

    public Fields(int entrySize, List<LField> fields) {
        if(entrySize != fields.size()) throw new RuntimeException("invalid Fields. the size doesn't match with num of entries");
        this.entrySize = entrySize;
        this.fields = fields;
    }

    public int size() {
        return 2 + fields.stream().map(LField::size).reduce(0, Integer::sum);
    }
}
