package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodHandle;

public class ConstantMethodHandlefResolver implements Resolver<ConstantMethodHandle> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodHandle entry) {
    entry.setReference(constantPool.findByIndex(entry.getReferenceIndex()));

    // TODO:

    entry.setResolved(true);
  }
}
