package dev.ishikawa.lovejvm.memory.methodarea;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

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

  /** @return the starting address of the give class */
  int lookupStaticAreaAddress(RawClass rawClass);

  RawMethod lookupMethod(ConstantMethodref constantMethodref);

  RawField lookupField(ConstantFieldref constantFieldref);

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

  RawClass lookupClass(ConstantClass constantClass);

  /** set 0 bits in the static area of the given class */
  void prepareStaticArea(RawClass rawClass);
}
