package dev.ishikawa.lovejvm.rawclass.field;


import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RawField {
  private final int accessFlag;
  private List<AccessFlag> accessFlagList = null;
  private final ConstantUtf8 name;
  private final ConstantUtf8 descriptor;
  private final Attrs attrs;

  public RawField(int accessFlag, ConstantUtf8 name, ConstantUtf8 descriptor, Attrs attrs) {
    this.accessFlag = accessFlag;
    this.name = name;
    this.descriptor = descriptor;
    this.attrs = attrs;
  }

  public int size() {
    return 2 // accessFlag
        + 2 // fieldName
        + 2 // fieldDesc
        + attrs.size(); // attrs
  }

  public boolean hasSignature(String fieldName) {
    return Objects.equals(this.name.getLabel(), fieldName);
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

  public int getRawAccessFlag() {
    return accessFlag;
  }

  public JvmType getJvmType() {
    return JvmType.findByJvmSignature(getDescriptor().getLabel());
  }

  public ConstantUtf8 getDescriptor() {
    return descriptor;
  }

  public ConstantUtf8 getName() {
    return name;
  }

  public boolean isStatic() {
    return (RawField.AccessFlag.STATIC.getBits() & accessFlag) > 0;
  }

  public Attrs getAttrs() {
    return attrs;
  }

  public enum AccessFlag {
    PUBLIC((short) 0x0001),
    PRIVATE((short) 0x0002),
    PROTECTED((short) 0x0004),
    STATIC((short) 0x0008),
    FINAL((short) 0x0010),
    VOLATILE((short) 0x0040),
    TRANSIENT((short) 0x0080),
    SYNTHETIC((short) 0x1000),
    ENUM((short) 0x4000);

    private final short bits;

    AccessFlag(short bits) {
      this.bits = bits;
    }

    public short getBits() {
      return bits;
    }
  }
}
