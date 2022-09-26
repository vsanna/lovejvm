package dev.ishikawa.lovejvm.rawclass;


import dev.ishikawa.lovejvm.LoveJVM;
import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
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
      String classLoaderName,
      int minorVersion,
      int majorVersion,
      ConstantPool constantPool,
      int accessFlag,
      ConstantClass thisClass,
      ConstantClass superClass,
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
    this.constantPool = constantPool;
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

  public Optional<RawMethod> findMethodBy(ConstantNameAndType nameAndType) {
    return methods.findBy(nameAndType.getName().getLabel(), nameAndType.getDescriptor().getLabel());
  }

  public Optional<RawMethod> findClinit() {
    return methods.findBy("<clinit>", "()V");
  }

  public Optional<RawMethod> findEntryPoint() {
    return methods.findStaticBy(LoveJVM.ENTRY_METHOD_NAME, LoveJVM.ENTRY_METHOD_DESC);
  }

  public Optional<RawField> findFieldBy(ConstantNameAndType nameAndType) {
    return fields.findBy(nameAndType.getName().getLabel());
  }

  public List<RawField> getStaticFields() {
    return fields.getStaticFields();
  }

  /**
   * @return binary size(bytes) of the classfile(not including static area). should be equal to
   *     this.raw.length()
   */
  public int getClassfileBytes() {
    return 4 // magic number
        + (2 + 2) // versions
        + constantPool.size() // constant pool
        + 2 // access flag
        + 2 // this class
        + 2 // super class
        + interfaces.size()
        + fields.size()
        + methods.size()
        + attrs.size();
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
        + constantPool.size() // constant pool
        + 2 // access flag
        + 2 // this class
        + 2 // super class
        + interfaces.size()
        + fields.size();
  }

  public int offsetToField(RawField rawField) {
    return getFields().offsetToFieldBytes(rawField);
  }

  public int offsetBytesToStaticArea() {
    return getClassfileBytes();
  }

  public int offsetBytesToStaticField(RawField rawField) {
    int result = offsetBytesToStaticArea();

    for (RawField staticField : fields.getStaticFields()) {
       if(staticField.equals(rawField)) break;

       result += staticField.getJvmType().wordSize() * Word.BYTES_SIZE;
    }

    return result;
  }

  public static final String CLINIT_METHOD_NAME = "<clinit>";
  public static final String CLINIT_METHOD_DESC = "()V";
  public static final String INIT_METHOD_NAME = "<init>";
  public static final String INIT_METHOD_DESC = "()V";
}
