package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPackage;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstantPackageResolver implements Resolver<ConstantPackage> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantPackage entry) {
    entry.setStringObjectId(RawSystem.stringPool.getOrCreate(entry.getName().getLabel()));
    entry.setResolved(true);
  }
}
