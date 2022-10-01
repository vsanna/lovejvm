package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.vm.RawSystem;

public class ConstClassResolver implements Resolver<ConstantClass> {

  /** when resolving a reference to other class, creation and loading of the class starts. */
  @Override
  public void resolve(ConstantPool constantPool, ConstantClass entry) {
    RawClass rawClass = RawSystem.methodAreaManager.lookupOrLoadClass(entry.getName().getLabel());
    int objectId = rawClass.getClassObjectId();

    RawSystem.classLinker.link(rawClass);
    RawSystem.classInitializer.initialize(rawClass);

    entry.setObjectId(objectId);
    entry.setResolved(true);
  }
}
