package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods;
import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods.LAttrBootstrapMethodsBody.LAttrBootstrapMethod;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantDynamic;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodHandle;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolLoadableEntry;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.stream.Collectors;

/** どういうときに使われるのかまだ良くわかっていない */
public class ConstantDynamicResolver implements Resolver<ConstantDynamic> {

  @Override
  public void resolve(ConstantPool constantPool, ConstantDynamic entry) {
    entry.setNameAndType(
        (ConstantNameAndType) constantPool.findByIndex(entry.getNameAndTypeIndex()));

    // 1. bootstrapMethodAttrIndexをもとに、RawClassのattrsにあるBootstrapMethods からentryを取得
    LAttrBootstrapMethod bootstrapMethod =
        constantPool.getRawClass().getAttrs().findAllBy(AttrName.BOOTSTRAP_METHODS).stream()
            .findFirst()
            .map(it -> (AttrBootstrapMethods) it)
            .orElseThrow(() -> new RuntimeException("BootstrapMethodsAttribute doesn't exist"))
            .getAttrBody()
            .findBy(entry.getBootstrapMethodAttrIndex());

    // 2. prep the bootstrap method
    List<List<Word>> arguments =
        bootstrapMethod.getBootstrapArguments().stream()
            .map(
                it -> {
                  it.resolve(constantPool);
                  return ((ConstantPoolLoadableEntry) it).loadableValue();
                })
            .collect(Collectors.toList());

    ConstantMethodHandle bootstrapMethodRef = bootstrapMethod.getBootstrapMethodRef();
    bootstrapMethodRef.resolve(constantPool);

    RawObject methodRef = RawSystem.heapManager.lookupObject(bootstrapMethodRef.getObjectId());

    // 3. execute the bootstrap method
    // java.lang.invoke.MethodRefからRawMethodを作る
    //   RawMethod bootstrapMethod = somehow(methodRef);
    // threadに渡して処理
    //   new RawThread("system:resolve").init(bootstrapMethod).run();
    // threadのroot stackのoperandStackに残った最後の値を採用
    //   thread.run()が最後の値List<Word>を返すようにする... word数わからん. rawMethodのdescriptorから?
    entry.setDynamicValue(List.of());

    entry.setResolved(true);
  }
}
