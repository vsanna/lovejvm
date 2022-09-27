package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;

public class ConstantMethodrefResolver implements Resolver<ConstantMethodref> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodref entry) {
    entry.setConstantClassRef((ConstantClass) constantPool.findByIndex(entry.getClassIndex()));
    entry.setNameAndType(
        (ConstantNameAndType) constantPool.findByIndex(entry.getNameAndTypeIndex()));

    // TODO:
    entry.setClassObjectId(0);
    entry.setMethodObjectId(0);

    entry.setResolved(true);
  }
}
