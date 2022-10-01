package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public interface ConstantPoolEntry {
  // get references to child elements
  default void shakeOut(ConstantPool constantPool) {
    /* noop as default */
  }

  ConstantPoolEntry resolve(ConstantPool constantPool);

  boolean isResolved();

  void setResolved(boolean isResolved);

  /** @return the size of BYTES for the constant pool entry */
  int size();
}
