package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

public class ConstClassResolver implements Resolver<ConstantClass> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantClass entry) {
    entry.setName((ConstantUtf8) constantPool.findByIndex(entry.getNameIndex()));

    // TODO: Class object
    entry.setObjectId(0);

    entry.setResolved(true);
  }
}
