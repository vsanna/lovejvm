package dev.ishikawa.lovejvm.memory.methodarea;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantFieldref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MethodAreaManagerImpl implements MethodAreaManager {
  // Map<String binaryName(ex: java/lang/String) to ClassEntry>
  private Map<String, ClassEntry> classMap = new HashMap<>();
  private final MethodArea methodArea = MethodAreaSimulator.INSTANCE;

  public MethodAreaManagerImpl() {}

  @Override
  public byte lookupByte(int address) {
    return methodArea.lookupByte(address);
  }

  @Override
  public void register(RawClass rawClass, byte[] classfile) {
    int startingAddress = methodArea.headAddress();

    // REFACTOR: According to the reference, The same name class can be loaded under different
    classMap.put(rawClass.getBinaryName(), new ClassEntry(startingAddress, rawClass));

    byte[] bytes = new byte[rawClass.getClassfileBytes() + rawClass.getStaticAreaWords()];
    System.arraycopy(classfile, 0, bytes, 0, classfile.length);

    methodArea.allocate(bytes);
  }

  @Override
  public int lookupCodeSectionAddress(RawMethod method) {
    RawClass rawClass = method.getKlass();
    var classAddr =
        Optional.ofNullable(classMap.get(rawClass.getBinaryName()))
            .map(it -> it.address)
            .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));

    var offsetToMethods = rawClass.offsetBytesToMethods();
    var offsetToMethod = rawClass.getMethods().offsetToMethod(method);
    var offsetToAttrs = method.offsetToAttrs();
    var offsetToAttr = method.getAttrs().offsetToAttr(AttrName.CODE);
    var offsetToInstructions = 14; // attrName ~ instruction length
    return classAddr
        + offsetToMethods
        + offsetToMethod
        + offsetToAttrs
        + offsetToAttr
        + offsetToInstructions;
  }

  @Override
  public int lookupStaticAreaAddress(RawClass rawClass) {
    var classAddr =
        Optional.ofNullable(classMap.get(rawClass.getBinaryName()))
            .map(it -> it.address)
            .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));

    return classAddr + rawClass.offsetBytesToStaticArea();
  }

  @Override
  public RawMethod lookupMethod(ConstantMethodref constantMethodref) {
    ConstantClass constantClassRef = constantMethodref.getConstantClassRef();
    RawClass lClass = lookupClass(constantClassRef);
    ConstantNameAndType nameAndTypeRef = constantMethodref.getNameAndType();
    return lClass
        .findMethodBy(nameAndTypeRef)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

  @Override
  public RawField lookupField(ConstantFieldref constantFieldref) {
    ConstantClass constantClassRef = constantFieldref.getConstantClassRef();
    RawClass lClass = lookupClass(constantClassRef);
    ConstantNameAndType nameAndTypeRef = constantFieldref.getNameAndType();
    return lClass
        .findFieldBy(nameAndTypeRef)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

  public RawClass lookupClass(ConstantClass constantClassRef) {
    return Optional.ofNullable(classMap.get(constantClassRef.getName().getLabel()))
        .map(it -> it.rawClass)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

  @Override
  public RawClass lookupOrLoadClass(String binaryName) {
    return Optional.ofNullable(classMap.get(binaryName))
        .map(it -> it.rawClass)
        .orElseGet(() -> RawSystem.bootstrapLoader.loadByBinaryName(binaryName));
  }

  @Override
  public void prepareStaticArea(RawClass rawClass) {
    int startingAddress = lookupStaticAreaAddress(rawClass);
    int staticAreaByteSize = rawClass.getStaticAreaWords() * Word.BYTES_SIZE;
    this.methodArea.clear(startingAddress, staticAreaByteSize);
  }

  public static final MethodAreaManager INSTANCE = new MethodAreaManagerImpl();

  private static class ClassEntry {
    private int address;
    private RawClass rawClass;

    public ClassEntry(int address, RawClass rawClass) {
      this.address = address;
      this.rawClass = rawClass;
    }

    public int getAddress() {
      return address;
    }

    public RawClass getRawClass() {
      return rawClass;
    }
  }

}