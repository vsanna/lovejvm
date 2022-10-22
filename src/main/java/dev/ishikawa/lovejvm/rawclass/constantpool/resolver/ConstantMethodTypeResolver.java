package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodType;
import dev.ishikawa.lovejvm.vm.InvokeHelper;

public class ConstantMethodTypeResolver implements Resolver<ConstantMethodType> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodType entry) {
    String descriptor = entry.getDescriptor().getLabel();
    int methodTypeObjectId = InvokeHelper.INSTANCE.createMethodTypeObject(descriptor);
    entry.setObjectId(methodTypeObjectId);
    entry.setResolved(true);
  }
}
