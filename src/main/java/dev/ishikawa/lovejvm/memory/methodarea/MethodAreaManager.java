package dev.ishikawa.lovejvm.memory.methodarea;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
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

  /** @return a byte located in the given address */
  byte lookupByte(int address);

  /** @return the starting address of the given method */
  int lookupCodeSectionAddress(RawMethod method);

  /** @return the offset from the head of the given method, to current pc */
  int lookupCodeSectionRelativeAddress(RawMethod method, int pc);

  /** @return the starting address of the give class */
  int lookupStaticAreaAddress(RawClass rawClass);

  RawMethod lookupAllMethod(String binaryName, String methodName, String methodDescriptor);

  RawMethod lookupAllMethodRecursively(
      String binaryName, String methodName, String methodDescriptor);

  RawMethod lookupStaticMethod(String binaryName, String methodName, String methodDescriptor);

  RawMethod lookupMemberMethod(String binaryName, String methodName, String methodDescriptor);

  RawField lookupAllField(String binaryName, String fieldName);

  RawField lookupMemberField(String binaryName, String fieldName);

  RawField lookupStaticField(String binaryName, String fieldName);
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
