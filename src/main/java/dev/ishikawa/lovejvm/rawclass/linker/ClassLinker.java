package dev.ishikawa.lovejvm.rawclass.linker;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.vm.RawSystem;

/**
 * 前提: - load済み - dynamic computed constant, invoke dynamic computed は特に何もしない when to trigger -
 * 上記前提を満たせばいつでもいい - 実質resolveが処理の中心であり、その手前でverify/prepareはやればいい(のかな?) -
 * なので、resolveが必要なタイミングでlinkとしてまとめてverify/prepare/resolveを行えば良さそう - つまり!! linkのタイミング =
 * resolveのタイミング. ConstantClassRefのresolveの中でついでにlinkしてしまう -
 * Create/Loadの際にsuperclass/superinterfacesへのConstantClassRefをresolve - 特定のoperand code: anewarray,
 * checkcast getfield putfield, instanceof invokedynamic invokeinterface [x] invokespecial [x]
 * invokestatic [x] invokevirtual, ldc, ldc_w, ldc2_w, multianewarray, [x] new, [x] getstatic [x]
 * putstatic what to do - verify: structure check - prepare: static areaにdefault値を入れる / method
 * table, object templatesというものも作るらしい(ref:
 * https://www.ibm.com/docs/en/sdk-java-technology/7.1?topic=uc-class-loading-1) - resolve: ...
 * ただしこれはlazyにできる. classのresolve = class objectのreferenceを返す
 *
 * @see dev.ishikawa.lovejvm.rawclass.constantpool.resolver.Resolver
 */
public class ClassLinker {
  public static final ClassLinker INSTANCE = new ClassLinker();

  private ClassLinker() {}

  public void link(RawClass targetClass) {
    if(targetClass instanceof RawArrayClass) return;

    if (RawSystem.methodAreaManager.lookupClass(targetClass.getBinaryName()).isEmpty()) {
      throw new RuntimeException(
          String.format(
              "This class is not loaded yet. classname: %s", targetClass.getBinaryName()));
    }
    if (targetClass.isLinked()) return;

    verify(targetClass); // TODO: get byte array from methodarea
    prepare(targetClass);
    resolve(targetClass);

    targetClass.setLinked(true);
  }

  private void verify(RawClass targetClass) {
    /* TODO: impl */
  }

  /**
   * set default values to each field according to each field's type ex: int -> 0, boolean -> false
   */
  private void prepare(RawClass targetClass) {
    RawSystem.methodAreaManager.prepareStaticArea(targetClass);

    // TODO: make method table, object templates
  }

  private void resolve(RawClass targetClass) {
    /* TODO: impl OR do resolution lazily */
  }
}
