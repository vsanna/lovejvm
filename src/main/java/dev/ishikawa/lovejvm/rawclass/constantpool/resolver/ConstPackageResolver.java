package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPackage;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstPackageResolver implements Resolver<ConstantPackage> {
  private final RawSystem rawSystem;

  public ConstPackageResolver(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  @Override
  public void resolve(ConstantPool constantPool, ConstantPackage entry) {
    entry.setStringObjectId(rawSystem.stringPool().getOrCreate(entry.getName().getLabel()));
    entry.setResolved(true);
  }
}
