package dev.ishikawa.lovejvm.rawclass.attr;

import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

public abstract class Attr<T> {
    private ConstantUtf8 attrName;
    private final int dataLength;
    private T attrBody;

    protected Attr(ConstantUtf8 attrName, int dataLength, T attrBody) {
        this.attrName = attrName;
        this.dataLength = dataLength;
        this.attrBody = attrBody;
    }

    public ConstantUtf8 getAttrName() {
        return attrName;
    }

    public T getAttrBody() {
        return attrBody;
    }

    public int size() {
        return 2 // attrName
                + 4 // num to show dataLength
                + dataLength;
    }
}