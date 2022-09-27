package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

/**
 * ConstantPoolUnresolvableEntry is a constant pool entry that doesn't need resolution steps. Such
 * entries have resolved value(basically, primary values) from the initializing phase.
 */
public abstract class ConstantPoolUnresolvableEntry implements ConstantPoolEntry {
  @Override
  public boolean isResolved() {
    return true;
  }

  @Override
  public void setResolved(boolean isResolved) {
    /* noop */
  }

  @Override
  public ConstantPoolEntry resolve(ConstantPool constantPool) {
    return this;
  }
}
