package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods;
import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods.LAttrBootstrapMethodsBody.LAttrBootstrapMethod;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInvokeDynamic;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolLoadableEntry;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InvokeDynamic is used for lambda.
 * Java compiler translate each lambda functions as statci methods first.
 * And then, it invokes those lambdas by utilizing ConstantInvokeDynamicResolver
 *
 * once the bootstrap method completes, it returns an instance of CallSite.
 * It contains a pointer to actual logic. This is represented as a MethodHandle
 * It also
 *
 * ref: https://www.baeldung.com/java-invoke-dynamic
 * */
public class ConstantInvokeDynamicResolver implements Resolver<ConstantInvokeDynamic> {
  @Override
  public void resolve(ConstantPool constantPool, ConstantInvokeDynamic entry) {
    /**
     * To resolve an unresolved symbolic reference to a call site specifier involves three steps:
     *
     * <p>A call site specifier gives a symbolic reference to a method handle which is to serve as
     * the bootstrap method for a dynamic call site. The method handle is resolved (§5.4.3.5) to
     * obtain a reference to an instance of java.lang.invoke.MethodHandle.
     *
     * <p>A call site specifier gives a method descriptor, TD. A reference to an instance of
     * java.lang.invoke.MethodType is obtained as if by resolution of a symbolic reference to a
     * method type (§5.4.3.5) with the same parameter and return types as TD.
     *
     * <p>A call site specifier gives zero or more static arguments, which communicate
     * application-specific metadata to the bootstrap method. Any static arguments which are
     * symbolic references to classes, method handles, or method types are resolved, as if by
     * invocation of the ldc instruction (§ldc), to obtain references to Class objects,
     * java.lang.invoke.MethodHandle objects, and java.lang.invoke.MethodType objects respectively.
     * Any static arguments that are string literals are used to obtain references to String
     * objects.
     *
     * <p>The result of call site specifier resolution is a tuple consisting of:
     *
     * <p>the reference to an instance of java.lang.invoke.MethodHandle,
     *
     * <p>the reference to an instance of java.lang.invoke.MethodType,
     *
     * <p>the references to instances of Class, java.lang.invoke.MethodHandle,
     * java.lang.invoke.MethodType, and String.
     */
    ConstantNameAndType nameAndType =
        (ConstantNameAndType) constantPool.findByIndex(entry.getNameAndTypeIndex());

    // TODO:
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
    RawObject methodRef =
        RawSystem.heapManager.lookupObject(bootstrapMethod.getBootstrapMethodRef().getObjectId());

    // 3. execute the bootstrap method
    // java.lang.invoke.MethodRefからRawMethodを作る
    //   RawMethod bootstrapMethod = somehow(methodRef);
    // threadに渡して処理
    //   new RawThread("system:resolve").init(bootstrapMethod).run();
    // threadのroot stackのoperandStackに残った最後の値を採用
    //   thread.run()が最後の値List<Word>を返すようにする... objectIdが返されるはず
    entry.setObjectId(0);

    entry.setResolved(true);
  }
}
