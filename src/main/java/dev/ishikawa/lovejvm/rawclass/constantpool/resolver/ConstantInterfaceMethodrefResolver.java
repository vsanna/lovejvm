package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantInterfaceMethodref;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.vm.RawSystem;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface#methodAをresolve
 *
 * step1: Cがinterfaceかチェック. non interfaceならIncompatibleClassChangeError
 * step2: Cが同じname/descのmethodを宣言していればそれ
 * step3: Objectがが同じname/descのmethodで、publicかつstaticでないものをもてばそれ
 * step4: そのname/descに対するmaximally-specific superinterface methodsを探す.
 *        abstractでないそれが唯一見つかればそれ
 * step5: どれかのsuperinterfaceがそのname/descでかつprivateでもstaticでもないmethodをもつ
 *        その中から任意に1つ選んでそれ
 * otherwise fail
 *
 * maximally-specific superinterface method:
 *   - direct or indirectなsuperinterfaceで定義されている
 *   - 同じname/descriptorをもつ
 *   - privateでもstaticでもない
 *   - interface I でそれが定義されているとするとき、そこに他に該当するmaximally-specific superinterface methodが存在しない
 * */
public class ConstantInterfaceMethodrefResolver implements Resolver<ConstantInterfaceMethodref> {
  @Override
  public void resolve(ConstantPool constantPool, ConstantInterfaceMethodref entry) {
    String binaryName = entry.getConstantClassRef().getName().getLabel();
    String methodName = entry.getNameAndType().getName().getLabel();
    String methodDescriptor = entry.getNameAndType().getDescriptor().getLabel();

    // TODO: check if this class is surely Interface

    RawClass targetInterface = RawSystem.methodAreaManager.lookupOrLoadClass(binaryName);
    int classObjectId = targetInterface.getClassObjectId();
    entry.setClassObjectId(classObjectId);

    var rawMethodOptional =
        targetInterface
            .findAllMethodBy(methodName, methodDescriptor)
            .or(
                () ->
                    RawSystem.methodAreaManager
                        .lookupOrLoadClass("java/lang/Object")
                        .findMemberMethodBy(methodName, methodDescriptor)
                        .filter(RawMethod::isPublic))
            .or(
                () -> {
                  var result =
                      new ArrayList<>(
                          RawSystem.methodAreaManager.lookupMaximallySpecificSuperinterfaceMethods(
                              binaryName, methodName, methodDescriptor));
                  if (result.size() == 1) {
                    return result.stream().findFirst();
                  } else {
                    return Optional.empty();
                  }
                })
            .or(
                () ->
                    RawSystem.methodAreaManager
                        .lookupMaximallySpecificSuperinterfaceMethods(
                            binaryName, methodName, methodDescriptor)
                        .stream()
                        .findFirst());

    rawMethodOptional.ifPresentOrElse(
        (rawMethod) -> {
          entry.setRawMethod(rawMethod);
          entry.setResolved(true);
        },
        () -> {
          throw new RuntimeException(
              String.format(
                  "Method lookup failed. %s, %s, %s", binaryName, methodName, methodDescriptor));
        });
  }
}
