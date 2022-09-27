package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public interface ConstantPoolEntry {
  ConstantPoolEntry resolve(ConstantPool constantPool);

  boolean isResolved();

  void setResolved(boolean isResolved);

  /** @return int the size of bytes for the constant pool entry */
  int size();
}
