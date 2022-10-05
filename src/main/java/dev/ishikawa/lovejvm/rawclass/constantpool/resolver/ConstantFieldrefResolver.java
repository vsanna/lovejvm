package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.vm.RawSystem;

/**
 * ConstantFieldref
 * When resolving a field reference, field resolution first attempts to look up the referenced field
 * in C and its superclasses:
 *
 * <p>If C declares a field with the name and descriptor specified by the field reference, field
 * lookup succeeds. The declared field is the result of the field lookup.
 *
 * <p>Otherwise, field lookup is applied recursively to the direct superinterfaces of the specified
 * class or interface C.
 *
 * <p>Otherwise, if C has a superclass S, field lookup is applied recursively to S.
 *
 * <p>Otherwise, field lookup fails.
 */
public class ConstantFieldrefResolver implements Resolver<ConstantFieldref> {
  @Override
  public void resolve(ConstantPool constantPool, ConstantFieldref entry) {
    String binaryName = entry.getConstantClassRef().getName().getLabel();
    String fieldName = entry.getNameAndType().getName().getLabel();

    RawClass targetClass = RawSystem.methodAreaManager.lookupOrLoadClass(binaryName);
    int classObjectId = targetClass.getClassObjectId();
    entry.setClassObjectId(classObjectId);

    var rawFieldOptional =
        // 1. C's field
        // 2. C's superinterfaces'fields
        RawSystem.methodAreaManager
            .lookupAllInterfaceFieldRecursively(binaryName, fieldName)
            .or(
                () -> {
                  // 3. C's superclass
                  return RawSystem.methodAreaManager.lookupAllFieldRecursively(
                      binaryName, fieldName);
                });

    rawFieldOptional.ifPresentOrElse(
        (rawField) -> {
          entry.setRawField(rawField);
          entry.setResolved(true);
        },
        () -> {
          throw new RuntimeException(
              String.format("Field lookup failed. %s, %s, %s", binaryName, fieldName));
        });
  }
}
