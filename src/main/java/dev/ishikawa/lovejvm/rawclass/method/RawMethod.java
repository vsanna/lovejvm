package dev.ishikawa.lovejvm.rawclass.method;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrCode;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RawMethod is an internal representation of one method.
 * the actual data(i.g. code section) are stored in MethodArea.
 * (Java's Method object is wrapper of this internal representation)
 * */
public class RawMethod {
  private Methods methods;

  private int accessFlag;
  private List<AccessFlag> accessFlagList = null;

  private ConstantUtf8 name;
  private ConstantUtf8 descriptor;
  private Attrs attrs;

  public RawMethod(int accessFlag, ConstantUtf8 name, ConstantUtf8 descriptor, Attrs attrs) {
    this.accessFlag = accessFlag;
    this.name = name;
    this.descriptor = descriptor;
    this.attrs = attrs;
  }

  public ConstantUtf8 getName() {
    return name;
  }

  public ConstantUtf8 getDescriptor() {
    return descriptor;
  }

  public int getOperandStackSize() {
    return getCode().map(AttrCode::getOperandStackSize).orElse(0);
  }

  public int getLocalsSize() {
    return getCode().map(AttrCode::getLocalsSize).orElse(0);
  }

  public Attrs getAttrs() {
    return attrs;
  }

  /**
   * If the method is either native or abstract, and is not a class or interface initialization
   * method, then its method_info structure must not have a Code attribute in its attributes table.
   * Otherwise, its method_info structure must have exactly one Code attribute in its attributes
   * table.
   */
  private Optional<AttrCode> getCode() {
    return attrs.findAllBy(AttrName.CODE).stream().map((it) -> (AttrCode) it).findFirst();
  }

  public boolean isStatic() {
    return (AccessFlag.STATIC.getBits() & accessFlag) > 0;
  }

  public int size() {
    return offsetToAttrs() + attrs.size(); // attrs
  }

  public int offsetToAttrs() {
    return 2 // accessFlag
        + 2 // methodName
        + 2; // methodDesc
  }

  public void setMethods(Methods methods) {
    this.methods = methods;
  }

  public RawClass getKlass() {
    return this.methods.getRawClass();
  }

  public boolean hasSignature(String methodName, String methodDescriptor) {
    return Objects.equals(this.getName().getLabel(), methodName)
        && Objects.equals(this.getDescriptor().getLabel(), methodDescriptor);
  }

  public List<AccessFlag> getAccessFlag() {
    if (Objects.isNull(accessFlagList)) {
      accessFlagList =
          Arrays.stream(AccessFlag.values())
              .filter(it -> (it.getBits() & accessFlag) > 0)
              .collect(Collectors.toList());
    }
    return accessFlagList;
  }

  public enum AccessFlag {
    PUBLIC((short) 0x0001),
    PRIVATE((short) 0x0002),
    PROTECTED((short) 0x0004),
    STATIC((short) 0x0008),
    FINAL((short) 0x0010),
    SYNCHRONIZED((short) 0x0020),
    BRIDGE((short) 0x0040),
    VARARGS((short) 0x0080),
    NATIVE((short) 0x0100),
    ABSTRACT((short) 0x0400),
    STRICT(
        (short)
            0x0800), // In a class file whose major version number is at least 46 and at most 60:
    // Declared strictfp.
    SYNTHETIC((short) 0x1000);

    private final short bits;

    AccessFlag(short bits) {
      this.bits = bits;
    }

    public short getBits() {
      return bits;
    }
  }
}
