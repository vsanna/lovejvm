package dev.ishikawa.lovejvm.memory.methodarea;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.linterface.RawInterface;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodAreaManagerImpl implements MethodAreaManager {
  // Map<String binaryName(ex: java/lang/String) to ClassEntry>
  private final Map<String, ClassEntry> classMap = new HashMap<>();
  private final MethodArea methodArea = MethodAreaSimulator.INSTANCE;

  public MethodAreaManagerImpl() {}

  @Override
  public byte lookupByte(int address) {
    return methodArea.lookupByte(address);
  }

  @Override
  public void register(RawClass rawClass, byte[] classfile) {
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
  public int lookupCodeSectionRelativeAddress(RawMethod method, int pc) {
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
  public RawMethod lookupAllMethod(String binaryName, String methodName, String methodDescriptor) {
    RawClass lClass = lookupOrLoadClass(binaryName);

    return lClass
        .findStaticMethodBy(methodName, methodDescriptor)
        .or(() -> lClass.findMemberMethodBy(methodName, methodDescriptor))
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Non existing method is looked up: %s", methodName)));
  }

  @Override
  public RawMethod lookupAllMethodRecursively(
      String binaryName, String methodName, String methodDescriptor) {
    return helper(binaryName, methodName, methodDescriptor)
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Non existing method is looked up: %s", methodName)));
  }

  public Optional<RawMethod> helper(String binaryName, String methodName, String methodDescriptor) {
    RawClass rawClass = lookupOrLoadClass(binaryName);

    Optional<RawMethod> rawMethodOptional =
        rawClass
            .findStaticMethodBy(methodName, methodDescriptor)
            .or(() -> rawClass.findMemberMethodBy(methodName, methodDescriptor));

    if (rawMethodOptional.isPresent()) {
      return rawMethodOptional;
    } else {
      // if rawClass is Object -> NotFound
      if (binaryName.equals("java/lang/Object")) {
        return Optional.empty();
      }

      // if superInterfaces.size == 0 -> superclass
      List<RawInterface> superInterfaces = rawClass.getInterfaces().getInterfaces();
      if (superInterfaces.size() == 0) {
        int a = 1;
        return helper("java/lang/Object", methodName, methodDescriptor);
      }

      for (RawInterface rawInterface : rawClass.getInterfaces().getInterfaces()) {
        // TODO: broad search instead of depth search
        var result =
            helper(
                rawInterface.getConstantClassRef().getName().getLabel(),
                methodName,
                methodDescriptor);
        if (result.isPresent()) {
          return result;
        }
      }

      return Optional.empty();
    }
  }

  @Override
  public RawMethod lookupStaticMethod(
      String binaryName, String methodName, String methodDescriptor) {
    RawClass lClass = lookupOrLoadClass(binaryName);
    // TODO: lookup recursively

    return lClass
        .findStaticMethodBy(methodName, methodDescriptor)
        .orElseThrow(
            () -> {
              String c = binaryName;
              String a = methodName;
              String b = methodDescriptor;
              int d = 1;
              return new RuntimeException(
                  String.format("Non existing method is looked up: %s", methodName));
            });
  }

  @Override
  public RawMethod lookupMemberMethod(
      String binaryName, String methodName, String methodDescriptor) {
    RawClass lClass = lookupOrLoadClass(binaryName);
    // TODO: lookup recursively

    return lClass
        .findMemberMethodBy(methodName, methodDescriptor)
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Non existing method is looked up: %s", methodName)));
  }

  @Override
  public RawField lookupAllField(String binaryName, String fieldName) {
    RawClass lClass = lookupOrLoadClass(binaryName);
    // TODO: lookup recursively.

    return lClass
        .findStaticFieldBy(fieldName)
        .or(() -> lClass.findMemberFieldBy(fieldName))
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Non existing field is looked up: %s", fieldName)));
  }

  @Override
  public RawField lookupStaticField(String binaryName, String fieldName) {
    RawClass lClass = lookupOrLoadClass(binaryName);
    // TODO: lookup recursively.

    return lClass
        .findStaticFieldBy(fieldName)
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Non existing field is looked up: %s", fieldName)));
  }

  @Override
  public RawField lookupMemberField(String binaryName, String fieldName) {
    RawClass lClass = lookupOrLoadClass(binaryName);
    // TODO: lookup recursively.

    return lClass
        .findMemberFieldBy(fieldName)
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("Non existing field is looked up: %s", fieldName)));
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
        .orElseGet(() -> RawSystem.bootstrapLoader.loadByBinaryName(binaryName));
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
              int classSize = rawClass.getRaw().length;
              int staticSize = rawClass.getStaticAreaWords() * Word.BYTES_SIZE;
              int objectSize = rawClass.getObjectWords() * Word.BYTES_SIZE;
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

  public static final MethodAreaManager INSTANCE = new MethodAreaManagerImpl();

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
