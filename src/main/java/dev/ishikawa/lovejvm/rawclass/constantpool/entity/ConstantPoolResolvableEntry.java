package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.resolver.ResolverService;

/**
 * ConstantPoolResolvableEntry is a constant pool entry that takes resolution steps. By resolving,
 * it gets a reference to some object(= objectId). It depends on each entry type what object is
 * referred to.
 */
public abstract class ConstantPoolResolvableEntry implements ConstantPoolEntry {
  private boolean isResolved = false;

  @Override
  public boolean isResolved() {
    return isResolved;
  }

  @Override
  public void setResolved(boolean isResolved) {
    this.isResolved = isResolved;
  }

  /**
   * resolve is called before the entry is retrieved from the constant pool. recursively until it
   * reaches the leaf element of the pool
   *
   * @return this
   */
  @Override
  public ConstantPoolEntry resolve(ConstantPool constantPool, ResolverService resolverService) {
    resolverService.resolve(constantPool, this);
    return this;
  }
}
