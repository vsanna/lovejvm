package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodType;
import dev.ishikawa.lovejvm.vm.InvokeHelper;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstantMethodTypeResolver implements Resolver<ConstantMethodType> {
  private final InvokeHelper invokeHelper;

  public ConstantMethodTypeResolver(RawSystem rawSystem) {
    this.invokeHelper = new InvokeHelper(rawSystem);
  }

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodType entry) {
    String descriptor = entry.getDescriptor().getLabel();
    int methodTypeObjectId = invokeHelper.createMethodTypeObject(descriptor);
    entry.setObjectId(methodTypeObjectId);
    entry.setResolved(true);
  }
}
