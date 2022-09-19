package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;

public abstract class LAttr<T> {
    private ConstantUtf8 attrName;
    private final int dataLength;
    private T attrBody;

    protected LAttr(ConstantUtf8 attrName, int dataLength, T attrBody) {
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
}