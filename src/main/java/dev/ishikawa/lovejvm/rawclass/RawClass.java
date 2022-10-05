package dev.ishikawa.lovejvm.rawclass;


import dev.ishikawa.lovejvm.LoveJVM;
import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.field.Fields;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.linterface.Interfaces;
import dev.ishikawa.lovejvm.rawclass.method.Methods;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.rawclass.parser.RawClassParser;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

/**
 * RawClass is an internal representation of class binary file + its static area the actual data are
 * stored in MethodArea. (Java's Class object is wrapper of this internal representation)
 */
public class RawClass {
  public RawClass(
      byte[] raw,
      String fullyQualifiedName,
      String name,
      String filename,
      String binaryName,
      @Nullable String classLoaderName,
      int minorVersion,
      int majorVersion,
      ConstantPool constantPool,
      int accessFlag,
      ConstantClass thisClass,
      @Nullable ConstantClass superClass,
      Interfaces interfaces,
      Fields fields,
      Methods methods,
      Attrs attrs) {
    this.raw = raw;

    this.fullyQualifiedName = fullyQualifiedName;
    this.name = name;
    this.filename = filename;
    this.binaryName = binaryName;
    this.classLoaderName = classLoaderName;

    this.minorVersion = minorVersion;
    this.majorVersion = majorVersion;

    // REFACTOR: make here better style
    this.constantPool = constantPool;
    this.constantPool.setRawClass(this);
    this.constantPool.shakeOut();

    this.accessFlag = accessFlag;
    this.thisClass = thisClass;

    if (superClass == null
        && !Objects.equals(
            thisClass.getName().getLabel(), RawClassParser.OBJECT_CLASS_BINARY_NAME)) {
      throw new RuntimeException("super class is missing");
    }
    this.superClass = superClass;

    this.interfaces = interfaces;

    this.fields = fields;
    this.fields.setRawClass(this);

    this.methods = methods;
    this.methods.setRawClass(this);

    this.attrs = attrs;
  }

  /**
   * raw bytes of the classfile Note: RawClass#raw field is duplicated data of bytes stored in
   * MethodArea. This is redundant. The raw field is just for debugging.
   */
  private final byte[] raw;

  private boolean isLinked = false;

  public boolean isLinked() {
    return isLinked;
  }

  public void setLinked(boolean linked) {
    isLinked = linked;
  }

  private ClassObjectStatus classObjectStatus = ClassObjectStatus.VERIFIED;

  public ClassObjectStatus getClassObjectStatus() {
    return classObjectStatus;
  }

  public void setClassObjectStatus(ClassObjectStatus classObjectStatus) {
    this.classObjectStatus = classObjectStatus;
  }

  public enum ClassObjectStatus {
    VERIFIED,
    BEING_INITIALIZED,
    INITIALIZED,
    ERROR
  }

  // reference to a Class object. this is used by ConstClassRef
  private int classObjectId;

  private final String fullyQualifiedName;
  private final String name;
  private final String filename;
  private final String binaryName;
  private final @Nullable String classLoaderName;

  private final int minorVersion;
  private final int majorVersion;

  private final ConstantPool constantPool;

  private final int accessFlag;

  private final ConstantClass thisClass;
  // Only Object doesn't have superClass
  private final @Nullable ConstantClass superClass;

  private final Interfaces interfaces;

  private final Fields fields;

  private final Methods methods;

  private final Attrs attrs;

  public byte[] getRaw() {
    return raw;
  }

  public void setClassObjectId(int classObjectId) {
    this.classObjectId = classObjectId;
  }

  public int getClassObjectId() {
    return classObjectId;
  }

  // ex: org.some.SomeApp
  public String getFullyQualifiedName() {
    return fullyQualifiedName;
  }
  // ex: SomeApp
  public String getName() {
    return name;
  }
  // ex: SomeApp.java
  public String getFilename() {
    return filename;
  }
  // ex: org/some/SomeApp
  public String getBinaryName() {
    return binaryName;
  }

  public int getMinorVersion() {
    return minorVersion;
  }

  public int getMajorVersion() {
    return majorVersion;
  }

  public ConstantPool getConstantPool() {
    return constantPool;
  }

  public int getAccessFlag() {
    return accessFlag;
  }

  public ConstantClass getThisClass() {
    return thisClass;
  }

  @Nullable
  public ConstantClass getSuperClass() {
    return superClass;
  }

  public Interfaces getInterfaces() {
    return interfaces;
  }

  public Fields getFields() {
    return fields;
  }

  public Methods getMethods() {
    return methods;
  }

  public Attrs getAttrs() {
    return attrs;
  }

  public @Nullable String getClassLoaderName() {
    return classLoaderName;
  }

  ///////////

  public Optional<RawMethod> findPolymorphicMethod(String methodName, String methodDescriptor) {
    // TODO: impl
    // https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-5.html#jvms-5.4.3.3
    return Optional.empty();
  }

  public Optional<RawMethod> findAllMethodBy(String methodName, String methodDescriptor) {
    return getMethods().findAllBy(methodName, methodDescriptor);
  }

  public Optional<RawMethod> findMemberMethodBy(String methodName, String methodDescriptor) {
    return getMethods().findMemberBy(methodName, methodDescriptor);
  }

  public Optional<RawMethod> findStaticMethodBy(String methodName, String methodDescriptor) {
    return getMethods().findStaticBy(methodName, methodDescriptor);
  }

  public Optional<RawField> findAllFieldBy(String fieldName) {
    return getFields().findAllBy(fieldName);
  }

  public Optional<RawField> findMemberFieldBy(String fieldName) {
    return getFields().findMemberBy(fieldName);
  }

  public Optional<RawField> findStaticFieldBy(String fieldName) {
    return getFields().findStaticBy(fieldName);
  }

  public Optional<RawMethod> findClinit() {
    return findStaticMethodBy(CLINIT_METHOD_NAME, CLINIT_METHOD_DESC);
  }

  public Optional<RawMethod> findInit(String descriptor) {
    return findMemberMethodBy(INIT_METHOD_NAME, descriptor);
  }

  public Optional<RawMethod> findEntryPoint() {
    return findStaticMethodBy(LoveJVM.ENTRY_METHOD_NAME, LoveJVM.ENTRY_METHOD_DESC);
  }

  public List<RawField> getMemberFields() {
    return fields.getMemberFields();
  }

  /**
   * @return binary size(bytes) of the classfile(not including static area). should be equal to
   *     this.raw.length()
   */
  public int getClassfileBytes() {
    return 4 // magic number
        + (2 + 2) // versions
        + getConstantPool().size() // constant pool
        + 2 // access flag
        + 2 // this class
        + 2 // super class
        + getInterfaces().size()
        + getFields().size()
        + getMethods().size()
        + getAttrs().size();
  }

  /** @return size(words) of mem space for static area of this class */
  public int getStaticAreaWords() {
    return getFields().getStaticFieldsMemWords();
  }

  /**
   * @return necessary binary size(words) for an object of this class 4byte ... ref to its super
   *     class's object size(field) * num of fields. TODO: how to put Array in heap.
   */
  public int getObjectWords() {
    return 1 // reference to superclass's object. 1 word
        + getFields().getMemberFieldsMemWords();
  }

  /** @return position of the initial byte in methods area. */
  public int offsetBytesToMethods() {
    return 4 // magic number
        + (2 + 2) // versions
        + getConstantPool().size() // constant pool
        + 2 // access flag
        + 2 // this class
        + 2 // super class
        + getInterfaces().size()
        + getFields().size();
  }

  public int offsetToMemberFieldBytes(RawField rawField) {
    return getFields().offsetToMemberFieldBytes(rawField);
  }

  public int offsetBytesToStaticArea() {
    return getClassfileBytes();
  }

  public int offsetBytesToStaticField(RawField rawField) {
    int result = offsetBytesToStaticArea();

    for (RawField staticField : fields.getStaticFields()) {
      if (staticField.equals(rawField)) break;

      result += staticField.getJvmType().wordSize() * Word.BYTES_SIZE;
    }

    return result;
  }

  public static final String CLINIT_METHOD_NAME = "<clinit>";
  public static final String CLINIT_METHOD_DESC = "()V";
  public static final String INIT_METHOD_NAME = "<init>";
  public static final String INIT_METHOD_DESC = "()V";
}
