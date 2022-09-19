package dev.ishikawa.lovejvm.klass.constantpool.entity;

import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;

public class ConstantFloat implements ConstantPoolEntry {
    private float floatValue; // 4bytes

    public ConstantFloat(float value) {
        this.floatValue = value;
    }

    public float getFloatValue() {
        return floatValue;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        // noop
    }

    @Override
    public boolean isResolved() {
        return true;
    }

    @Override
    public int size() {
        return 5;
    }
}
