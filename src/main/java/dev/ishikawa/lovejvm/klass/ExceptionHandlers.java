package dev.ishikawa.lovejvm.klass;

import java.util.List;

public class ExceptionHandlers {
    private final int size;
    private List<LExceptionHandler> fields;

    public ExceptionHandlers(int size, List<LExceptionHandler> fields) {
        if(size != fields.size()) throw new RuntimeException("invalid ExceptionHandlers. the size doesn't match with num of entries");
        this.size = size;
        this.fields = fields;
    }

    public int getSize() {
        return size;
    }
}
