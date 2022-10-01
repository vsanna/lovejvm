package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;

public class ConstantNameAndTypeResolver implements Resolver<ConstantNameAndType> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantNameAndType entry) {
    // TODO
    entry.setNameStringObjectId(0);
    entry.setDescriptorStringObjectId(0);

    entry.setResolved(true);
  }
}
