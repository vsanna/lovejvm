package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.resolver.ResolverService;

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
  public ConstantPoolEntry resolve(ConstantPool constantPool, ResolverService resolverService) {
    return this;
  }
}
