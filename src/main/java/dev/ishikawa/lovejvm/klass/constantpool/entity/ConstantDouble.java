package dev.ishikawa.lovejvm.klass.constantpool.entity;

import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;

public class ConstantDouble implements ConstantPoolEntry {
    private double doubleValue; // 8bytes

    public ConstantDouble(double value) {
        this.doubleValue = value;
    }

    public double getDoubleValue() {
        return doubleValue;
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