package dev.ishikawa.lovejvm.rawclass.type;

import static dev.ishikawa.lovejvm.rawclass.RawClass.ClassObjectStatus.INITIALIZED;
import static dev.ishikawa.lovejvm.rawclass.type.JvmType.primaryTypes;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.field.Fields;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.linterface.Interfaces;
import dev.ishikawa.lovejvm.rawclass.method.Methods;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.RawSystem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

/**
 * RawClass is an internal representation of class binary file + its static area the actual data are
 * stored in MethodArea. (Java's Class object is wrapper of this internal representation)
 *
 *
 * 勘違いしてたかも. 次元数*componentTypeで1クラスか?
 *
 * > The Java Virtual Machine creates a new array class with the indicated component type and number of dimensions.
 *
 * はい、そうでした
 */
public class RawArrayClass extends RawClass {
  private RawArrayClass(
      String fullyQualifiedName,
      String name,
      String binaryName,
      RawClass componentComplexClass,
      JvmType componentPrimaryType,
      int dimension) {
    super(
        null,
        fullyQualifiedName,
        name,
        null,
        binaryName,
        null,
        0,
        0,
        null,
        0,
        null,
        null,
        null,
        null,
        null,
        null);

    assert Objects.isNull(componentComplexClass) ^ Objects.isNull(componentPrimaryType);
    this.componentComplexClass = componentComplexClass;
    this.componentPrimaryType = componentPrimaryType;

    this.dimension = dimension;

    this.setLinked(true);
    this.setClassObjectStatus(INITIALIZED);
  }

  @Nullable private final RawClass componentComplexClass;
  @Nullable private final JvmType componentPrimaryType;
  private final int dimension;

  // 要素1つが占めるword sizeを返す
  // Class or Array -> Referenceとして子を持つ
  // Others(=Primitive) -> そのtypeに応じたサイズ
  public int getComponentWordSize() {
    return Optional.ofNullable(getComponentComplexClass())
        .map(it -> JvmType.OBJECT_REFERENCE.wordSize())
        .or(() -> Optional.ofNullable(getComponentPrimaryType()).map(JvmType::wordSize))
        .orElseThrow(() -> new RuntimeException("Invalid component type"));
  }

  @Nullable
  public RawClass getComponentComplexClass() {
    return componentComplexClass;
  }

  @Nullable
  public JvmType getComponentPrimaryType() {
    return componentPrimaryType;
  }

  @Override
  public Optional<RawClass> getRawSuperClass() {
    return RawSystem.methodAreaManager.lookupClass("java/lang/Object");
  }

  public int getDimension() {
    return dimension;
  }

  @Override
  public byte[] getRaw() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have raw");
  }

  @Override
  public String getFilename() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have Filename");
  }

  @Override
  public int getMinorVersion() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have minorVersion");
  }

  @Override
  public int getMajorVersion() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have majorVersion");
  }

  @Override
  public ConstantPool getConstantPool() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have constantPool");
  }

  @Override
  public int getAccessFlag() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have accessFlag");
  }

  @Override
  public ConstantClass getThisClass() {
    throw new UnsupportedOperationException(
        "RawArrayClass doesn't have thisClass constantPool entry");
  }

  @Override
  public @Nullable ConstantClass getSuperClass() {
    throw new UnsupportedOperationException(
        "RawArrayClass doesn't have superClass constantPool entry");
  }

  @Override
  public Interfaces getInterfaces() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have interfaces");
  }

  @Override
  public Fields getFields() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have fields");
  }

  @Override
  public Methods getMethods() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have methods");
  }

  @Override
  public Attrs getAttrs() {
    throw new UnsupportedOperationException("RawArrayClass doesn't have attrs");
  }

  @Override
  public Optional<RawMethod> findPolymorphicMethod(String methodName, String methodDescriptor) {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findPolymorphicMethod");
  }

  @Override
  public Optional<RawMethod> findAllMethodBy(String methodName, String methodDescriptor) {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findAllMethodBy");
  }

  @Override
  public Optional<RawMethod> findMemberMethodBy(String methodName, String methodDescriptor) {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findMemberMethodBy");
  }

  @Override
  public Optional<RawMethod> findStaticMethodBy(String methodName, String methodDescriptor) {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findStaticMethodBy");
  }

  @Override
  public Optional<RawField> findAllFieldBy(String fieldName) {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findAllFieldBy");
  }

  @Override
  public Optional<RawField> findMemberFieldBy(String fieldName) {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findMemberFieldBy");
  }

  @Override
  public Optional<RawField> findStaticFieldBy(String fieldName) {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findStaticFieldBy");
  }

  @Override
  public Optional<RawMethod> findClinit() {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findClinit");
  }

  @Override
  public Optional<RawMethod> findInit(String descriptor) {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findInit");
  }

  @Override
  public Optional<RawMethod> findEntryPoint() {
    throw new UnsupportedOperationException("RawArrayClass doesn't support findEntryPoint");
  }

  @Override
  public List<RawField> getMemberFields() {
    throw new UnsupportedOperationException("RawArrayClass doesn't support getMemberFields");
  }

  @Override
  public int getClassfileBytes() {
    throw new UnsupportedOperationException("RawArrayClass doesn't support getClassfileBytes");
  }

  @Override
  public int getStaticAreaWords() {
    throw new UnsupportedOperationException("RawArrayClass doesn't support getStaticAreaWords");
  }

  @Override
  public int getObjectWords() {
    throw new UnsupportedOperationException("RawArrayClass doesn't support getObjectWords");
  }

  @Override
  public int offsetBytesToMethods() {
    throw new UnsupportedOperationException("RawArrayClass doesn't support offsetBytesToMethods");
  }

  @Override
  public int offsetToMemberFieldBytes(RawField rawField) {
    throw new UnsupportedOperationException(
        "RawArrayClass doesn't support offsetToMemberFieldBytes");
  }

  @Override
  public int offsetBytesToStaticArea() {
    throw new UnsupportedOperationException(
        "RawArrayClass doesn't support offsetBytesToStaticArea");
  }

  @Override
  public int offsetBytesToStaticField(RawField rawField) {
    throw new UnsupportedOperationException(
        "RawArrayClass doesn't support offsetBytesToStaticField");
  }

  @Override
  public int getClassByteSize() {
    return 1;
  }

  @Override
  public int getStaticByteSize() {
    return 0;
  }

  @Override
  public int getObjectByteSize() {
    return -1;
  }

  // TODO: this should be in MethodAreaManager
  public static RawArrayClass lookupOrCreateRawArrayClass(String binaryName) {
    return RawSystem.methodAreaManager
        .lookupClass(binaryName)
        .map(it -> (RawArrayClass) it)
        .orElseGet(
            () -> {
              // 先頭の[の個数を数える = dimension
              // それを除いた残り = primary or complex の binaryNameのはず
              int dimension = 0;
              for (int i = 0; i < binaryName.toCharArray().length; i++) {
                if (binaryName.toCharArray()[i] == '[') {
                  dimension += 1;
                } else {
                  break;
                }
              }

              String elementTypeBinaryName = binaryName.substring(dimension);
              // elementTypeBinaryNameの先頭がJvmTypeのprimaryを示すなら
              JvmType jvmSignature =
                  JvmType.findByJvmSignature(elementTypeBinaryName.substring(0, 1));
              if (primaryTypes.contains(jvmSignature)) {
                return lookupOrCreatePrimaryRawArrayClass(jvmSignature, dimension);
              } else {
                // omit the heading 'L', and trailing ';'
                var elementRawClass =
                    RawSystem.methodAreaManager.lookupOrLoadClass(
                        elementTypeBinaryName.substring(1, elementTypeBinaryName.length() - 1));
                return lookupOrCreateComplexRawArrayClass(elementRawClass, dimension);
              }
            });
  }

  // TODO: this should be in MethodAreaManager
  public static RawArrayClass lookupOrCreatePrimaryRawArrayClass(JvmType jvmType, int dimension) {
    if (!primaryTypes.contains(jvmType)) {
      throw new IllegalArgumentException(String.format("%s is not primary type", jvmType));
    }
    String binaryName = "[".repeat(dimension) + jvmType.getJvmSignature();
    return RawSystem.methodAreaManager
        .lookupClass(binaryName)
        .map(it -> (RawArrayClass) it)
        .orElseGet(() -> createRawArrayClass(binaryName, null, jvmType, dimension));
  }

  // TODO: this should be in MethodAreaManager
  public static RawArrayClass lookupOrCreateComplexRawArrayClass(RawClass elementRawClass, int dimension) {
    String binaryName = "[".repeat(dimension) + "L" + elementRawClass.getBinaryName() + ";";
    return RawSystem.methodAreaManager
        .lookupClass(binaryName)
        .map(it -> (RawArrayClass) it)
        .orElseGet(() -> createRawArrayClass(binaryName, elementRawClass, null, dimension));
  }

  private static RawArrayClass createRawArrayClass(
      String binaryName,
      @Nullable RawClass componentComplexClass,
      @Nullable JvmType componentPrimaryType,
      int dimension) {
    var rawArrayClass =
        new RawArrayClass(
            binaryName,
            binaryName,
            binaryName,
            componentComplexClass,
            componentPrimaryType,
            dimension);
    RawSystem.methodAreaManager.registerArrayClass(rawArrayClass);
    RawObject classObject = RawSystem.heapManager.createClassObject(rawArrayClass);
    rawArrayClass.setClassObjectId(classObject.getObjectId());

    return rawArrayClass;
  }
}
