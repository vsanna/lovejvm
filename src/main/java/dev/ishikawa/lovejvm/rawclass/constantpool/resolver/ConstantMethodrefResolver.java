package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.vm.RawSystem;
import java.util.ArrayList;
import java.util.Optional;

/**
 * C#methodAをresolve
 *
 * step1: Cがinterfaceかチェック. interfaceならIncompatibleClassChangeError
 * step2:
 *   1. Cをresolve
 *   2. if Cが、同名かつsignature polymorphic method をもっていれば
 *     - descriptorにあるargsのclassをresolveしてlookup完了
 *   3. if Cが、同名かつ同じdescriptorを持つmethodをもっていれば
 *     - lookup終了
 *   4. if Cがsuperclassを持てば、step2を1から繰り返し
 * step3: Cの直接のinterfacesを順にみる
 *   - name/descが一致するabstractでないmaximally-specific superinterface methodがある -> それ
 *   - どれかのsuperinterfaceが同名同じdescriptorでかつprivateでもstaticでもないmethodをもつ -> それ
 *     - 条件の4店味を満たしていない. 複数候補がある場合、任意のどれかを選択する、と言っている。
 *
 * maximally-specific superinterface method:
 *   - direct or indirectなsuperinterfaceで定義されている
 *   - 同じname/descriptorをもつ
 *   - privateでもstaticでもない
 *   - interface I でそれが定義されているとするとき、そこに他に該当するmaximally-specific superinterface methodが存在しない
 * */
public class ConstantMethodrefResolver implements Resolver<ConstantMethodref> {
  private final RawSystem rawSystem;

  public ConstantMethodrefResolver(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  @Override
  public void resolve(ConstantPool constantPool, ConstantMethodref entry) {
    String binaryName = entry.getConstantClassRef().getName().getLabel();
    String methodName = entry.getNameAndType().getName().getLabel();
    String methodDescriptor = entry.getNameAndType().getDescriptor().getLabel();

    // TODO: check if this class is surely not Interface

    RawClass targetClass = rawSystem.methodAreaManager().lookupOrLoadClass(binaryName);
    int classObjectId = targetClass.getClassObjectId();
    entry.setClassObjectId(classObjectId);

    var rawMethodOptional =
        rawSystem.methodAreaManager()
            .lookupAllMethodRecursively(binaryName, methodName, methodDescriptor)
            .or(
                () -> {
                  var result =
                      new ArrayList<>(
                          rawSystem.methodAreaManager().lookupMaximallySpecificSuperinterfaceMethods(
                              binaryName, methodName, methodDescriptor));
                  if (result.size() == 1) {
                    return result.stream().findFirst();
                  } else {
                    return Optional.empty();
                  }
                })
            .or(
                () ->
                    rawSystem.methodAreaManager()
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
