package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstantMethodrefResolver implements Resolver<ConstantMethodref> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodref entry) {
    RawClass rawClass =
        RawSystem.methodAreaManager.lookupOrLoadClass(
            entry.getConstantClassRef().getName().getLabel());
    int classObjectId = rawClass.getClassObjectId();

    entry.getConstantClassRef().resolve(constantPool);

    entry.setClassObjectId(classObjectId);

    RawMethod rawMethod = getRawMethod(entry);
    RawClass methodRawClass = getMethodRawClass();
    var methodObjectId = RawSystem.heapManager.register(methodRawClass);

    // TODO: init the object manually
    // use rawMethod to set it up.

    entry.setMethodObjectId(methodObjectId);

    entry.setResolved(true);
  }

  private RawMethod getRawMethod(ConstantMethodref entry) {
    return RawSystem.methodAreaManager.lookupAllMethod(
        entry.getConstantClassRef().getName().getLabel(),
        entry.getNameAndType().getName().getLabel(),
        entry.getNameAndType().getDescriptor().getLabel());
  }

  private RawClass getMethodRawClass() {
    String methodBinaryClassName = "java/lang/reflect/Method";
    return RawSystem.methodAreaManager.lookupOrLoadClass(methodBinaryClassName);
  }
}
