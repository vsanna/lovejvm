package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods;
import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods.AttrBootstrapMethodsBody.AttrBootstrapMethod;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantDouble;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantDynamic;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFloat;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInteger;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInvokeDynamic;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantLong;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodHandle;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolLoadableEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantString;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.InvokeHelper;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.RawThread;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.ArrayList;
import java.util.List;

/**
 * InvokeDynamic is used for lambda.
 * Java compiler translates each lambda functions as a static method.
 * And then, it invokes those lambdas by utilizing ConstantInvokeDynamicResolver
 *
 * once the bootstrap method completes, it returns an instance of CallSite.
 * It contains a pointer to actual logic. This is represented as a MethodHandle
 *
 * ref: https://www.baeldung.com/java-invoke-dynamic
 * */
public class ConstantInvokeDynamicResolver implements Resolver<ConstantInvokeDynamic> {
  private final RawSystem rawSystem;
  private final InvokeHelper invokeHelper;

  public ConstantInvokeDynamicResolver(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
    this.invokeHelper = new InvokeHelper(rawSystem);
  }

  @Override
  public void resolve(ConstantPool constantPool, ConstantInvokeDynamic entry) {
    entry.setNameAndType(
        (ConstantNameAndType) constantPool.findByIndex(entry.getNameAndTypeIndex()));

    // 1. First, R is examined to determine which code will serve as its bootstrap method,
    // and which arguments will be passed to that code.
    AttrBootstrapMethod bootstrapMethod =
        constantPool.getRawClass().getAttrs().findAllBy(AttrName.BOOTSTRAP_METHODS).stream()
            .findFirst()
            .map(it -> (AttrBootstrapMethods) it)
            .orElseThrow(() -> new RuntimeException("BootstrapMethodsAttribute doesn't exist"))
            .getAttrBody()
            .findBy(entry.getBootstrapMethodAttrIndex());

    // 1-1. bootstrap method handle is resolved
    ConstantMethodHandle bootstrapMethodHandle = bootstrapMethod.getBootstrapMethodRef();
    bootstrapMethodHandle.resolve(constantPool, rawSystem.resolverService()); // MethodHandle = Methodrefをwrapしたもの、を取得

    // 1-2. (this is for CONSTANT_ConstDynamic. skipped)

    // 1-3. If R is a symbolic reference to a dynamically-computed call site,
    // then it gives a method descriptor.
    // SEE: Table 5.4.3.5-B. Method Descriptors for Method Handles
    // https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-5.html#jvms-5.4.3.5-250

    // NOTE: このdescriptorはlambdaのdescriptor
    ConstantNameAndType descriptor = entry.getNameAndType();
    int methodTypeObjectId =
        invokeHelper.createMethodTypeObject(descriptor.getDescriptor().getLabel());
    entry.setMethodTypeObjectId(methodTypeObjectId);

    // 1-4. R gives zero or more static arguments,
    // which communicate application-specific metadata to the bootstrap method.
    // Each static argument A is resolved, in the order given by R, as follows:
    //
    //   If A is a string constant, then a reference to its instance of class String is obtained.
    //   If A is a numeric constant, then a reference to an instance of
    // java.lang.invoke.MethodHandle is obtained by the following procedure:
    //     Let v be the value of the numeric constant, and let T be a field descriptor which
    // corresponds to the type of the numeric constant.
    //     Let MH be a method handle produced as if by invocation of the identity method of
    // java.lang.invoke.MethodHandles with an argument representing the class Object.
    //     A reference to an instance of java.lang.invoke.MethodHandle is obtained as if by the
    // invocation MH.invoke(v) with method descriptor (T)Ljava/lang/Object;.
    //   If A is a symbolic reference to a dynamically-computed constant
    //     with a field descriptor indicating a primitive type T,
    //     then A is resolved, producing a primitive value v. Given v and T,
    //     a reference is obtained to an instance of java.lang.invoke.MethodHandle
    //     according to the procedure specified above for numeric constants.
    //   If A is any other kind of symbolic reference, then the result is the result of resolving A.

    List<ConstantPoolEntry> bootstrapArguments = bootstrapMethod.getBootstrapArguments();

    List<Word> arguments = new ArrayList<>();

    // Lookup(not to be used now)
    arguments.add(Word.of(RawObject.NULL_ID));
    // String(methodName)
    arguments.add(Word.of(rawSystem.stringPool().getOrCreate(descriptor.getName().getLabel())));
    // MethodType(methodDescriptor)
    arguments.add(Word.of(methodTypeObjectId));

    bootstrapArguments.forEach(
        it -> {
          if (it instanceof ConstantString) {
            it.resolve(constantPool, rawSystem.resolverService());
            arguments.addAll(((ConstantPoolLoadableEntry) it).loadableValue());
          }

          if (it instanceof ConstantInteger
              || it instanceof ConstantLong
              || it instanceof ConstantFloat
              || it instanceof ConstantDouble) {
            // TODO
            //   If A is a numeric constant, then a reference to an instance of
            // java.lang.invoke.MethodHandle is obtained by the following procedure:
            //     Let v be the value of the numeric constant, and let T be a field descriptor which
            // corresponds to the type of the numeric constant.
            //     Let MH be a method handle produced as if by invocation of the identity method of
            // java.lang.invoke.MethodHandles with an argument representing the class Object.
            //     A reference to an instance of java.lang.invoke.MethodHandle is obtained as if by
            // the invocation MH.invoke(v) with method descriptor (T)Ljava/lang/Object;.
            arguments.addAll(List.of(Word.of(1)));
          }

          if (it instanceof ConstantDynamic) {
            // TODO
            //   If A is a symbolic reference to a dynamically-computed constant with a field
            // descriptor indicating a primitive type T,
            //   then A is resolved, producing a primitive value v.
            //   Given v and T, a reference is obtained to an instance of
            // java.lang.invoke.MethodHandle according to the procedure specified above for numeric
            // constants.
            arguments.addAll(List.of(Word.of(1)));
          }

          if (it instanceof ConstantMethodHandle) {
            entry.setMethodRef((ConstantMethodref) ((ConstantMethodHandle) it).getReference());
          }

          it.resolve(constantPool, rawSystem.resolverService());
          arguments.addAll(((ConstantPoolLoadableEntry) it).loadableValue());
        });

    // Second, the arguments are packaged into an array and the bootstrap method is invoked.
    // ここまででBSMのmethodrefとargumentsが手に入った. それらを用いて、まずBSMを実行する(invokeWithArguments相当)
    var bsmThread = rawSystem.createThread("BSM");
    bsmThread.invoke(
        ((ConstantMethodref) bootstrapMethodHandle.getReference()).getRawMethod(), arguments);
    // metafactory()がcallsiteを返す
    int callsiteObjectId = bsmThread.getOutput().get(0).getValue();

    // Third, the result of the bootstrap method(=CallSite) is validated, and used as the result of
    // resolution.
    entry.setCallSiteObjectId(callsiteObjectId);

    entry.setResolved(true);
  }
}
