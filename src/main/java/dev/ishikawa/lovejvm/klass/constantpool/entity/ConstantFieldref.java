package dev.ishikawa.lovejvm.klass.constantpool.entity;

import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;

public class ConstantFieldref implements ConstantPoolEntry {
    private boolean isResolved = false;

    private int classIndex; // 2bytes
    private ConstantClass klass;

    private int nameAndTypeIndex; // 2byte
    private ConstantNameAndType nameAndType;

    public ConstantFieldref(int classIndex, int nameAndTypeIndex) {
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    public ConstantNameAndType getNameAndType() {
        if(!isResolved()) throw new RuntimeException("not resolved yet");
        return nameAndType;
    }

    public ConstantClass getKlass() {
        if(!isResolved()) throw new RuntimeException("not resolved yet");
        return klass;
    }

    @Override
    public void resolve(ConstantPool constantPool) {
        this.klass = (ConstantClass) constantPool.findByIndex(classIndex);
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
