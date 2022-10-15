package dev.ishikawa.lovejvm.rawclass.initializer;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.RawClass.ClassObjectStatus;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.linterface.RawInterface;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.RawThread;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 目的: - (lockとって) classのstaticスペースに値を入れていく = clinit実行する - それを再帰的に(まずは親から)実行していく - 終わったらclass
 * object(ここに至るまえに存在する前提)のstatusをfully initializedに更新する - class
 * objectのstatusがin-progressだからと止まっている他threadをresumeする trigger [x] new ... constClassRef.resolve()
 * [x] getstatic ... constFieldRef.resolve() [x] putstatic ... constFeidlRef.resolve() [x]
 * invokestatic ... constMethodRef.resolve() - MethodHandle instance invoke, - subclass initialize
 * or implementation initialize - entrypoint class loading
 */
public class ClassInitializer {
  public static final ClassInitializer INSTANCE = new ClassInitializer();

  private ClassInitializer() {}

  public void initialize(RawClass targetClass) {
    if (targetClass instanceof RawArrayClass) return;

    // 0. check class object status
    // TODO: get lock;
    switch (targetClass.getClassObjectStatus()) {
      case VERIFIED:
        targetClass.setClassObjectStatus(ClassObjectStatus.BEING_INITIALIZED);
        break;
      case BEING_INITIALIZED:
        // TODO: block the caller
        return;
      case INITIALIZED:
        // nothing to do
        return;
      case ERROR:
        throw new RuntimeException(
            String.format(
                "This class has got error while initializing. %s", targetClass.getBinaryName()));
    }
    // TODO: release lock

    // 1. initialize superclass / superinterfaces
    initializeRelatedClasses(targetClass);

    // 2. run <clinit> to set static values
    targetClass
        .findClinit()
        .ifPresent(
            (clinit) -> {
              // TODO: stop all the other thread.
              new RawThread("system:clinit:" + targetClass.getBinaryName()).init(clinit).run();
            });

    targetClass.setClassObjectStatus(ClassObjectStatus.INITIALIZED);

    // TODO: notify other thread waiting for this class
  }

  /** load its direct superclass, its direct super interfaces, and its element's class if it has. */
  private void initializeRelatedClasses(RawClass targetClass) {
    Optional.ofNullable(targetClass.getSuperClass()).ifPresent(this::initializeFromConstantClass);

    targetClass.getInterfaces().getInterfaces().stream()
        .map(RawInterface::getConstantClassRef)
        .collect(Collectors.toList())
        .forEach(this::initializeFromConstantClass);

    if (targetClass instanceof RawArrayClass) {
      Optional.ofNullable(((RawArrayClass) targetClass).getComponentComplexClass())
          .ifPresent(this::initialize);
    }
  }

  private void initializeFromConstantClass(ConstantClass constantClass) {
    String superClassBinaryName = constantClass.getName().getLabel();
    RawClass rawSuperClass = RawSystem.methodAreaManager.lookupOrLoadClass(superClassBinaryName);
    initialize(rawSuperClass);
  }
}
