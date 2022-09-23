package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantUtf8 implements ConstantPoolEntry {
    private String label;

    public ConstantUtf8(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
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
        return 3 + label.getBytes().length;
    }
}
