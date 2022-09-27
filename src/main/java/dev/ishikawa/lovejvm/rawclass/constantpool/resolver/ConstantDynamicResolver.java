package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantDynamic;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;

public class ConstantDynamicResolver implements Resolver<ConstantDynamic> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantDynamic entry) {
    entry.setNameAndType(
        (ConstantNameAndType) constantPool.findByIndex(entry.getNameAndTypeIndex()));

    // TODO:

    entry.setResolved(true);
  }
}
