package dev.ishikawa.lovejvm.klass;

import java.util.List;

public class Fields {
    private final int size;
    private List<LField> fields;

    public Fields(int size, List<LField> fields) {
        if(size != fields.size()) throw new RuntimeException("invalid Fields. the size doesn't match with num of entries");
        this.size = size;
        this.fields = fields;
    }

    public int getSize() {
        return size;
    }
}
