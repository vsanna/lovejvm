package dev.ishikawa.lovejvm.rawclass.constantpool.entity;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.resolver.ResolverService;

public interface ConstantPoolEntry {
  // get references to child elements
  default void shakeOut(ConstantPool constantPool) {
    /* noop as default */
  }

  boolean isResolved();

  void setResolved(boolean isResolved);

  /** @return the size of BYTES for the constant pool entry */
  int size();

  ConstantPoolEntry resolve(ConstantPool constantPool, ResolverService resolverService);
}
