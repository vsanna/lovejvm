package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInterfaceMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;

public class ConstantInterfaceMethodrefResolver implements Resolver<ConstantInterfaceMethodref> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantInterfaceMethodref entry) {
    entry.setConstantClassRef((ConstantClass) constantPool.findByIndex(entry.getClassIndex()));
    entry.setNameAndType(
        (ConstantNameAndType) constantPool.findByIndex(entry.getNameAndTypeIndex()));

    // TODO:
    entry.setClassObjectId(0);
    entry.setMethodObjectId(0);

    entry.setResolved(true);
  }
}
