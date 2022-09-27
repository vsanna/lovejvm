package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodType;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstantMethodTypeResolver implements Resolver<ConstantMethodType> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodType entry) {
    ConstantUtf8 label = (ConstantUtf8) constantPool.findByIndex(entry.getDescriptorIndex());
    entry.setLabel(label);
    entry.setStringObjectId(RawSystem.stringPool.getOrCreate(label.getLabel()));
    entry.setResolved(true);
  }
}
