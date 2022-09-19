package dev.ishikawa.lovejvm.klass.constantpool.entity;

import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;

public class ConstantString implements ConstantPoolEntry {
    private boolean isResolved = false;

    private int stringIndex; // 2bytes
    private ConstantUtf8 string;

    public ConstantString(int nameIndex) {
        this.stringIndex = nameIndex;
    }

    public ConstantUtf8 getString() {
        if(!isResolved()) throw new RuntimeException("not resolved yet");
        return string;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.string = (ConstantUtf8) constantPool.findByIndex(stringIndex);
        isResolved = true;
    }

    @Override
    public boolean isResolved() {
        return isResolved;
    }

    @Override
    public int size() {
        return 3;
    }
}
