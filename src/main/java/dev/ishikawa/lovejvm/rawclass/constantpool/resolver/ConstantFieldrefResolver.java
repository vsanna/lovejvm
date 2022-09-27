package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;

public class ConstantFieldrefResolver implements Resolver<ConstantFieldref> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantFieldref entry) {
    entry.setConstantClassRef((ConstantClass) constantPool.findByIndex(entry.getClassIndex()));
    entry.setNameAndType(
        (ConstantNameAndType) constantPool.findByIndex(entry.getNameAndTypeIndex()));

    // TODO:
    entry.setClassObjectId(0);
    entry.setFieldObjectId(0);

    entry.setResolved(true);
  }
}
