package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantString;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstStringResolver implements Resolver<ConstantString> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantString entry) {
    entry.setObjectId(RawSystem.stringPool.getOrCreate(entry.getLabel().getLabel()));
    entry.setResolved(true);
  }
}
