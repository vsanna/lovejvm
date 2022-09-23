package dev.ishikawa.lovejvm.rawclass.constantpool.entity;

import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public class ConstantMethodref implements ConstantPoolEntry {
    private boolean isResolved = false;

    private int classIndex; // 2bytes
    private ConstantClass constantClassRef;

    private int nameAndTypeIndex; // 2byte
    private ConstantNameAndType nameAndType;

    public ConstantMethodref(int classIndex, int nameAndTypeIndex) {
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    public ConstantNameAndType getNameAndType() {
        if(!isResolved()) throw new RuntimeException("not resolved yet");
        return nameAndType;
    }

    public ConstantClass getConstantClassRef() {
        if(!isResolved()) throw new RuntimeException("not resolved yet");
        return constantClassRef;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.constantClassRef = (ConstantClass) constantPool.findByIndex(classIndex);
        this.nameAndType = (ConstantNameAndType) constantPool.findByIndex(nameAndTypeIndex);
        isResolved = true;
    }

    @Override
    public boolean isResolved() {
        return isResolved;
    }

    @Override
    public int size() {
        return 5;
    }
}
