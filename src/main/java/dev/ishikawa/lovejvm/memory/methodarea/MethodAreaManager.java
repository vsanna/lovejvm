package dev.ishikawa.lovejvm.memory.methodarea;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import dev.ishikawa.lovejvm.vm.RawSystem;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * MethodAreaManager manages MethodArea, and provides util methods so that
 * caller doesn't have to care binary details
 * */
public interface MethodAreaManager  {
  /**
   * register method allocates memory space
   * for the classfile bytes and static area for the class
   * with its metadata = RawClass.
   * static area is calculated by the given rawClass
   * */
  void register(RawClass rawClass, byte[] classfile);

  /**
   * @return a byte located in the given address
   * */
  byte lookupByte(int address);

  /**
   * @return the starting address of the given method
   * */
  int lookupCodeSectionAddress(RawMethod method);

  /**
   * @return the starting address of the give class
   * */
  int lookupStaticAreaAddress(RawClass rawClass);

  RawMethod lookupMethod(ConstantMethodref constantMethodref);

  RawField lookupField(ConstantFieldref constantFieldref);

  RawClass lookupOrLoadClass(String binaryName);

  /**
   * set 0 bits in the static area of the given class
   * */
  void prepareStaticArea(RawClass rawClass);
}
