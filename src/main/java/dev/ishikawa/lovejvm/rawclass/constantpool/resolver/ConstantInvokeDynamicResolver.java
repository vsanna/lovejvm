package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInvokeDynamic;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;

public class ConstantInvokeDynamicResolver implements Resolver<ConstantInvokeDynamic> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantInvokeDynamic entry) {
    entry.setNameAndType(
        (ConstantNameAndType) constantPool.findByIndex(entry.getNameAndTypeIndex()));

    // TODO:

    entry.setResolved(true);
  }
}
