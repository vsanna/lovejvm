package dev.ishikawa.lovejvm.klass.constantpool.entity;

import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;

public class ConstantInteger implements ConstantPoolEntry {
    private int intValue; // 4bytes

    public ConstantInteger(int value) {
        this.intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        // noop
    }

    @Override
    public boolean isResolved() {
        return true;
    }
}
