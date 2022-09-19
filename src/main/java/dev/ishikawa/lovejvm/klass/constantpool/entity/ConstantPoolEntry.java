package dev.ishikawa.lovejvm.klass.constantpool.entity;

import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;

/**
 * marker interface
 * */
public interface ConstantPoolEntry {
    /**
     * resolve is called before the entry is retrieved from the constant pool
     * resolve occurs recursively until it reaches the leaf element of the pool
     * */
    void resolve(ConstantPool constantPool);
    boolean isResolved();
}
