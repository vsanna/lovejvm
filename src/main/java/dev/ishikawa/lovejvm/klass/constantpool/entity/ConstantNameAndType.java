package dev.ishikawa.lovejvm.klass.constantpool.entity;

import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;

public class ConstantNameAndType implements ConstantPoolEntry {
    private boolean isResolved = false;

    private int nameIndex; // 2bytes
    private ConstantUtf8 name;

    private int descriptorIndex; // 2bytes
    private ConstantUtf8 descriptor;


    public ConstantNameAndType(int nameIndex, int descriptorIndex) {
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
    }

    public ConstantUtf8 getName() {
        if(!isResolved()) throw new RuntimeException("not resolved yet");
        return name;
    }

    public ConstantUtf8 getDescriptor() {
        if(!isResolved()) throw new RuntimeException("not resolved yet");
        return descriptor;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.name = (ConstantUtf8) constantPool.findByIndex(nameIndex);
        this.descriptor = (ConstantUtf8) constantPool.findByIndex(descriptorIndex);
        isResolved = true;
    }

    @Override
    public boolean isResolved() {
        return isResolved;
    }
}
