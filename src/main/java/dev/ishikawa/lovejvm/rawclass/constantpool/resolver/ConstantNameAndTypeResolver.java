package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

public class ConstantNameAndTypeResolver implements Resolver<ConstantNameAndType> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantNameAndType entry) {
    ConstantUtf8 nameLabel = (ConstantUtf8) constantPool.findByIndex(entry.getNameIndex());
    entry.setName(nameLabel);

    ConstantUtf8 descriptorLabel =
        (ConstantUtf8) constantPool.findByIndex(entry.getDescriptorIndex());
    entry.setDescriptor(descriptorLabel);

    // TODO
    entry.setNameStringObjectId(0);
    entry.setDescriptorStringObjectId(0);

    entry.setResolved(true);
  }
}
