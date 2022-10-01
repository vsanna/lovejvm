package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodType;

public class ConstantMethodTypeResolver implements Resolver<ConstantMethodType> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodType entry) {
    // TODO: init MethodType
    // prep ClassLoader ...
    // call MethodType fromMethodDescriptorString(String descriptor, ClassLoader loader)
    entry.setObjectId(0);

    entry.setResolved(true);
  }
}
