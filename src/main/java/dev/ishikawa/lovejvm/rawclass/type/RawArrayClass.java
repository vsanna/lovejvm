package dev.ishikawa.lovejvm.rawclass.type;


import dev.ishikawa.lovejvm.rawclass.RawClass;

/**
 * RawClass is an internal representation of class binary file + its static area the actual data are
 * stored in MethodArea. (Java's Class object is wrapper of this internal representation)
 */
public class RawArrayClass extends RawClass {
  public RawArrayClass(
      String fullyQualifiedName,
      String name,
      String binaryName,
      RawClass componentComplexClass,
      JvmType componentPrimaryType,
      int size) {
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
    this.componentComplexClass = componentComplexClass;
    this.componentPrimaryType = componentPrimaryType;
    this.size = size;
  }

  private final RawClass componentComplexClass;
  private final JvmType componentPrimaryType;
  private final int size;

  public RawClass getComponentComplexClass() {
    return componentComplexClass;
  }

  public JvmType getComponentPrimaryType() {
    return componentPrimaryType;
  }

  public int getSize() {
    return size;
  }
}
