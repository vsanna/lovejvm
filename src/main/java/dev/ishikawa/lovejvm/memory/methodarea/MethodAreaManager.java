package dev.ishikawa.lovejvm.memory.methodarea;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.Optional;

/**
 * MethodAreaManager manages MethodArea, and provides util methods so that caller doesn't have to
 * care binary details
 */
public interface MethodAreaManager {
  /**
   * register method allocates memory space for the classfile bytes and static area for the class
   * with its metadata = RawClass. static area is calculated by the given rawClass
   */
  void register(RawClass rawClass, byte[] classfile);

  void registerArrayClass(RawArrayClass rawArrayClass);

  /** @return a byte located in the given address */
  byte lookupByte(int address);

  /** @return the starting address of the given method */
  int lookupCodeSectionAddress(RawMethod method);

  /** @return the offset from the head of the given method, to current pc */
  int lookupCodeSectionRelativeAddress(RawMethod method, int pc);

  /** @return the starting address of the give class */
  int lookupStaticAreaAddress(RawClass rawClass);

  /**
   * 5.4.6. Method Selection During execution of an invokeinterface or invokevirtual
   * instruction, a method is selected with respect to (i) the run-time type of the object
   * on the stack, and (ii) a method that was previously resolved by the instruction. The
   * rules to select a method with respect to a class or interface C and a method mR are
   * as follows:
   *
   * <p>If mR is marked ACC_PRIVATE, then it is the selected method.
   *
   * <p>Otherwise, the selected method is determined by the following lookup procedure:
   *
   * <p>If C contains a declaration of an instance method m that can override mR (§5.4.5),
   * then m is the selected method.
   *
   * <p>Otherwise, if C has a superclass, a search for a declaration of an instance method
   * that can override mR is performed, starting with the direct superclass of C and
   * continuing with the direct superclass of that class, and so forth, until a method is
   * found or no further superclasses exist. If a method is found, it is the selected
   * method.
   *
   * <p>Otherwise, the maximally-specific superinterface methods of C are determined
   * (§5.4.3.3). If exactly one matches mR's name and descriptor and is not abstract, then
   * it is the selected method.
   *
   * <p>Any maximally-specific superinterface method selected in this step can override
   * mR; there is no need to check this explicitly.
   *
   * <p>While C will typically be a class, it may be an interface when these rules are
   * applied during preparation (§5.4.2).
   */
  RawMethod selectMethod(RawClass startingClass, RawMethod targetMethod);

  List<RawMethod> lookupMaximallySpecificSuperinterfaceMethods(
      String binaryName, String methodName, String methodDescriptor);

  Optional<RawMethod> lookupAllMethodRecursively(
      String binaryName, String methodName, String methodDescriptor);

  Optional<RawField> lookupAllInterfaceFieldRecursively(String binaryName, String methodName);

  Optional<RawField> lookupAllFieldRecursively(String binaryName, String fieldName);

  /**
   * getStaticFieldValue calculates offset to the field space in MethodArea(static area) and then
   * get the value
   *
   * @return one word or two word(when the value is long or double)
   */
  List<Word> getStaticFieldValue(RawClass rawClass, RawField rawField);

  /**
   * putStaticFieldValue calculates offset to the field space in MethodArea(static area) and then
   * store the value
   */
  void putStaticFieldValue(RawClass rawClass, RawField rawField, List<Word> value);

  RawClass lookupOrLoadClass(String binaryName);

  Optional<RawClass> lookupClass(String binaryName);

  RawClass lookupClass(ConstantClass constantClass);

  /** set 0 bits in the static area of the given class */
  void prepareStaticArea(RawClass rawClass);

  void dump();
}
