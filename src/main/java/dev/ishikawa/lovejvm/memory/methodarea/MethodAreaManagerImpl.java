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
import java.util.List;
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

    byte[] bytes =
        new byte[rawClass.getClassfileBytes() + rawClass.getStaticAreaWords() * Word.BYTES_SIZE];
    System.arraycopy(classfile, 0, bytes, 0, classfile.length);

    methodArea.allocate(bytes);
  }

  @Override
  public int lookupCodeSectionAddress(RawMethod method) {
    /*
     * If the method is either native or abstract,
     *     and is not a class or interface initialization method,
     * then its method_info structure must not have a Code attribute in its attributes table.
     * */
    if ((method.isAbstract() || method.isNative()) && !method.isClinit()) {
      return -1;
    }

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
    var classAddr = getClassAddress(rawClass);
    return classAddr + rawClass.offsetBytesToStaticArea();
  }

  @Override
  public RawMethod lookupMethod(ConstantMethodref constantMethodref) {
    ConstantClass constantClassRef = constantMethodref.getConstantClassRef();
    RawClass lClass = lookupOrLoadClass(constantClassRef.getName().getLabel());
    ConstantNameAndType nameAndTypeRef = constantMethodref.getNameAndType();
    return lClass
        .findMethodBy(nameAndTypeRef)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

  @Override
  public RawField lookupField(ConstantFieldref constantFieldref) {
    ConstantClass constantClassRef = constantFieldref.getConstantClassRef();
    RawClass lClass = lookupOrLoadClass(constantClassRef.getName().getLabel());
    ConstantNameAndType nameAndTypeRef = constantFieldref.getNameAndType();
    return lClass
        .findFieldBy(nameAndTypeRef)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

  @Override
  public List<Word> getStaticFieldValue(RawClass rawClass, RawField rawField) {
    var startingAddress = getClassAddress(rawClass) + rawClass.offsetBytesToStaticField(rawField);

    var fieldByteSize = rawField.getJvmType().wordSize() * Word.BYTES_SIZE;
    var bytes = methodArea.retrieve(startingAddress, fieldByteSize);
    return Word.of(bytes);
  }

  @Override
  public void putStaticFieldValue(RawClass rawClass, RawField rawField, List<Word> words) {
    var startingAddress = getClassAddress(rawClass) + rawClass.offsetBytesToStaticField(rawField);

    methodArea.save(startingAddress, Word.toByteArray(words));
  }

  @Override
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

  private int getClassAddress(RawClass rawClass) {
    return Optional.ofNullable(classMap.get(rawClass.getBinaryName()))
        .map(it -> it.address)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
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
