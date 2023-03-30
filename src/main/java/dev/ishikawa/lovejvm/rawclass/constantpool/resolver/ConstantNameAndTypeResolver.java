package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstantNameAndTypeResolver implements Resolver<ConstantNameAndType> {
  private final RawSystem rawSystem;

  public ConstantNameAndTypeResolver(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  @Override
  public void resolve(ConstantPool constantPool, ConstantNameAndType entry) {
    entry.setNameStringObjectId(rawSystem.stringPool().getOrCreate(entry.getName().getLabel()));
    entry.setDescriptorStringObjectId(
        rawSystem.stringPool().getOrCreate(entry.getDescriptor().getLabel()));
    entry.setResolved(true);
  }
}
