package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPackage;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstPackageResolver implements Resolver<ConstantPackage> {
  @Override
  public void resolve(ConstantPool constantPool, ConstantPackage entry) {
    ConstantUtf8 label = (ConstantUtf8) constantPool.findByIndex(entry.getNameIndex());
    entry.setName(label);
    entry.setStringObjectId(RawSystem.stringPool.getOrCreate(label.getLabel()));
    entry.setResolved(true);
  }
}
