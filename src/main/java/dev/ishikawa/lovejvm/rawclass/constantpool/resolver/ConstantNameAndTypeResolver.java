package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstantNameAndTypeResolver implements Resolver<ConstantNameAndType> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantNameAndType entry) {
    entry.setNameStringObjectId(RawSystem.stringPool.getOrCreate(entry.getName().getLabel()));
    entry.setDescriptorStringObjectId(
        RawSystem.stringPool.getOrCreate(entry.getDescriptor().getLabel()));
    entry.setResolved(true);
  }
}
