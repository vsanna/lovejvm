package dev.ishikawa.lovejvm.memory.methodarea;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.linterface.RawInterface;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodAreaManagerImpl implements MethodAreaManager {
  // Map<String binaryName(ex: java/lang/String) to ClassEntry>
  private final Map<String, ClassEntry> classMap = new HashMap<>();
  private final MethodArea methodArea = new MethodAreaSimulator();
  private final RawSystem rawSystem;
  
  public MethodAreaManagerImpl(RawSystem rawSystem) {
    this.rawSystem = rawSystem;  
  }

  @Override
  public byte lookupByte(int address) {
    return methodArea.lookupByte(address);
  }

  @Override
  public void registerClass(RawClass rawClass, byte[] classfile) {
    if (classMap.containsKey(rawClass.getBinaryName())) return;

    int startingAddress = methodArea.headAddress();

    // REFACTOR: According to the jvm spec, The same name class can be loaded under different
    classMap.put(rawClass.getBinaryName(), new ClassEntry(startingAddress, rawClass));

    byte[] bytes =
        new byte[rawClass.getClassfileBytes() + rawClass.getStaticAreaWords() * Word.BYTES_SIZE];
    System.arraycopy(classfile, 0, bytes, 0, classfile.length);

    methodArea.allocate(bytes);
  }

  @Override
  public void registerArrayClass(RawArrayClass rawArrayClass) {
    if (classMap.containsKey(rawArrayClass.getBinaryName())) return;

    int startingAddress = methodArea.headAddress();

    // REFACTOR: According to the jvm spec, The same name class can be loaded under different
    classMap.put(rawArrayClass.getBinaryName(), new ClassEntry(startingAddress, rawArrayClass));

    // consume only 1 byte for the arrayClass
    byte[] bytes = new byte[] {0};
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
  public int getOffsetFromCodeSectionToPc(RawMethod method, int pc) {
    if ((method.isAbstract() || method.isNative()) && !method.isClinit()) {
      return -1;
    }

    return pc - lookupCodeSectionAddress(method);
  }

  @Override
  public int lookupStaticAreaAddress(RawClass rawClass) {
    var classAddr = getClassAddress(rawClass);
    return classAddr + rawClass.offsetBytesToStaticArea();
  }

  @Override
  public RawMethod selectMethod(RawClass startingClass, RawMethod targetMethod) {
    if (targetMethod.isPrivate()) {
      return targetMethod;
    }

    return startingClass
        .getMethods()
        .findAllBy(targetMethod.getName().getLabel(), targetMethod.getDescriptor().getLabel())
        .or(() -> selectMethodHelper(startingClass, targetMethod))
        .or(
            () -> {
              List<RawMethod> mssMethods =
                  lookupMaximallySpecificSuperinterfaceMethods(
                          startingClass.getBinaryName(),
                          targetMethod.getName().getLabel(),
                          targetMethod.getDescriptor().getLabel())
                      .stream()
                      .filter(it -> !it.isAbstract())
                      .collect(Collectors.toList());
              if (mssMethods.size() == 1) {
                return mssMethods.stream().findFirst();
              } else {
                return Optional.empty();
              }
            })
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format(
                        "method selection failed. %s, %s, %s",
                        startingClass.getBinaryName(),
                        targetMethod.getName().getLabel(),
                        targetMethod.getDescriptor().getLabel())));
  }

  private Optional<RawMethod> selectMethodHelper(RawClass rawClass, RawMethod targetMethod) {
    return rawClass
        .getMethods()
        .findAllBy(targetMethod.getName().getLabel(), targetMethod.getDescriptor().getLabel())
        .or(() -> rawClass.getRawSuperClass(rawSystem).flatMap(it -> selectMethodHelper(it, targetMethod)));
  }

  /**
   * mainly for ConstMethodRef resolution
   * */
  @Override
  public List<RawMethod> lookupMaximallySpecificSuperinterfaceMethods(
      String binaryName, String methodName, String methodDescriptor) {
    RawClass rawClass =
        lookupClass(binaryName)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("Non existing class is looked up methods. %s", binaryName)));
    return rawClass.getInterfaces().getInterfaces().stream()
        .map(
            it ->
                lookupMaximallySpecificSuperinterfaceMethodsHelper(
                    it, methodName, methodDescriptor))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private List<RawMethod> lookupMaximallySpecificSuperinterfaceMethodsHelper(
      RawInterface aInterface, String methodName, String methodDescriptor) {
    RawClass rawClass = lookupOrLoadClass(aInterface.getConstantClassRef().getName().getLabel());
    var methods =
        rawClass.getInterfaces().getInterfaces().stream()
            .map(
                aaInterface ->
                    lookupMaximallySpecificSuperinterfaceMethodsHelper(
                        aaInterface, methodName, methodDescriptor))
            .flatMap(Collection::stream)
            .filter(RawMethod::isPublic)
            .collect(Collectors.toList());

    rawClass
        .findMemberMethodBy(methodName, methodDescriptor)
        .filter(RawMethod::isPublic)
        .ifPresent(methods::add);

    return methods;
  }

  /**
   * 親をたどっていく. 実際のmethodを探す工程
   * mainly for ConstMethodRef resolution
   * */
  @Override
  public Optional<RawMethod> lookupAllMethodRecursively(
      String binaryName, String methodName, String methodDescriptor) {
    RawClass targetClass = rawSystem.methodAreaManager().lookupOrLoadClass(binaryName);

    return targetClass
        .findPolymorphicMethod(methodName, methodDescriptor)
        .map(
            it -> {
              // TODO: if polymorphic method is found,
              //  classes in the descriptor should be resolved.
              return it;
            })
        .or(() -> targetClass.findAllMethodBy(methodName, methodDescriptor))
        .or(
            () -> {
              return targetClass
                  .getRawSuperClass(rawSystem)
                  .flatMap(
                      superClass ->
                          lookupAllMethodRecursively(
                              superClass.getBinaryName(), methodName, methodDescriptor));
            });
  }

  /**
   *
   * mainly for ConstMethodRef resolution
   * */
  @Override
  public Optional<RawField> lookupAllInterfaceFieldRecursively(
      String binaryName, String fieldName) {
    RawClass targetClass = rawSystem.methodAreaManager().lookupOrLoadClass(binaryName);

    return targetClass
        .findAllFieldBy(fieldName)
        .or(
            () -> {
              return targetClass.getInterfaces().getInterfaces().stream()
                  .map(
                      aInterface ->
                          lookupAllInterfaceFieldRecursively(
                              aInterface.getConstantClassRef().getName().getLabel(), fieldName))
                  .flatMap(Optional::stream)
                  .findFirst();
            });
  }

  @Override
  public Optional<RawField> lookupAllFieldRecursively(String binaryName, String fieldName) {
    RawClass rawClass = lookupOrLoadClass(binaryName);

    return rawClass
        .findAllFieldBy(fieldName)
        .or(
            () ->
                rawClass
                    .getRawSuperClass(rawSystem)
                    .flatMap(it -> lookupAllFieldRecursively(it.getBinaryName(), fieldName)));
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
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format(
                        "Non existing class is tried to load: %s",
                        constantClassRef.getName().getLabel())));
  }

  @Override
  public RawClass lookupOrLoadClass(String binaryName) {
    return Optional.ofNullable(classMap.get(binaryName))
        .map(it -> it.rawClass)
        .orElseGet(
            () -> {
              if (binaryName.startsWith("[")) {
                return RawArrayClass.lookupOrCreateRawArrayClass(binaryName, rawSystem);
              } else {
                try {
                  return rawSystem.bootstrapLoader().load(binaryName);
                } catch (Exception ex) {
                  int a = 1;
                  throw ex;
                }
              }
            });
  }

  @Override
  public Optional<RawClass> lookupClass(String binaryName) {
    return Optional.ofNullable(classMap.get(binaryName)).map(it -> it.rawClass);
  }

  @Override
  public void prepareStaticArea(RawClass rawClass) {
    int startingAddress = lookupStaticAreaAddress(rawClass);
    int staticAreaByteSize = rawClass.getStaticAreaWords() * Word.BYTES_SIZE;
    this.methodArea.clear(startingAddress, staticAreaByteSize);
  }

  @Override
  public void dump() {
    System.out.println("[MA DUMP] addr| className|csize|ssize|osize");

    classMap.entrySet().stream()
        .sorted(Comparator.comparingInt(a -> a.getValue().getAddress()))
        .forEach(
            (record) -> {
              ClassEntry entry = record.getValue();
              // address|RawClass name|class size|static size|object size
              int address = entry.getAddress();
              RawClass rawClass = entry.getRawClass();
              int classSize = rawClass.getClassByteSize();
              int staticSize = rawClass.getStaticByteSize();
              int objectSize = rawClass.getObjectByteSize();
              System.out.printf(
                  "[MA DUMP]%5d|%20s|%5d|%5d|%5d\n",
                  address,
                  rawClass.getName().substring(0, Math.min(rawClass.getName().length(), 18)),
                  classSize,
                  staticSize,
                  objectSize);
            });
  }

  private int getClassAddress(RawClass rawClass) {
    return Optional.ofNullable(classMap.get(rawClass.getBinaryName()))
        .map(it -> it.address)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

  private static class ClassEntry {
    private final int address;
    private final RawClass rawClass;

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
