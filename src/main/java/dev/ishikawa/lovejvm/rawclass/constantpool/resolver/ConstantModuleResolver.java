package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantModule;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstantModuleResolver implements Resolver<ConstantModule> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantModule entry) {
    ConstantUtf8 label = entry.getLabel();
    entry.setStringObjectId(RawSystem.stringPool.getOrCreate(label.getLabel()));
    entry.setResolved(true);
  }
}
