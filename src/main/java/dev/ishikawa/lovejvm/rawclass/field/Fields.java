package dev.ishikawa.lovejvm.rawclass.field;

import java.util.List;

public class Fields {
    private final int entrySize;
    private List<RawField> fields;

    public Fields(int entrySize, List<RawField> fields) {
        if(entrySize != fields.size()) throw new RuntimeException("invalid Fields. the size doesn't match with num of entries");
        this.entrySize = entrySize;
        this.fields = fields;
    }

    public int size() {
        return 2 + fields.stream().map(RawField::size).reduce(0, Integer::sum);
    }
}
