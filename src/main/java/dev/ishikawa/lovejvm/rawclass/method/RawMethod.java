package dev.ishikawa.lovejvm.rawclass.method;

import static dev.ishikawa.lovejvm.rawclass.RawClass.CLINIT_METHOD_NAME;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrCode;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.rawclass.field.RawField.AccessFlag;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RawMethod is an internal representation of one method. the actual data(i.g. code section) are
 * stored in MethodArea. (Java's Method object is wrapper of this internal representation)
 */
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

  /**
   * TODO: why not using
   * @return num of words to transfer when invoking a new method */
  public int getTransitWordSize(boolean hasReceiver) {
    // TODO: is it possible to know hasReceiver in this method?
    int argumentsWordSize = getArgumentsWordSize();
    if (hasReceiver) {
      return argumentsWordSize + JvmType.OBJECT_REFERENCE.wordSize();
    }
    return argumentsWordSize;
  }

  /** TODO: this should be fully tested */
  private int getArgumentsWordSize() {
    // ex: ([Ljava/lang/String;[[IDLjava/lang/String;II)V -> [, [, D, L, I, I
    // [ -> L ~ ;まで飛ばす or 次の1文字飛ばす
    // L -> ~; まで飛ばす
    // 他 -> なにもしない
    var argumentString = this.descriptor.getLabel().split("\\(")[1].split("\\)")[0];
    var argumentCharArray = argumentString.toCharArray();
    List<JvmType> argumentTypes = new ArrayList<>();

    for (int i = 0; i < argumentCharArray.length; i++) {
      char c = argumentCharArray[i];

      switch (c) {
        case '[':
          i++;
          if (argumentCharArray[i] != 'L') {
            break;
          }
        case 'L':
          {
            int j = 1;
            while (true) {
              char c2 = argumentCharArray[i + j];
              if (c2 == ';') break;
              j++;
            }
            i = i + j;
            break;
          }
        default:
          break;
      }

      argumentTypes.add(JvmType.findByJvmSignature(String.valueOf(c)));
    }

    return argumentTypes.stream().map(JvmType::wordSize).reduce(0, Integer::sum);
  }

  public JvmType getReturningType() {
    var returningValueType = this.descriptor.getLabel().split("\\(")[1].split("\\)")[1];
    return JvmType.findByJvmSignature(returningValueType);
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

  public boolean isPublic() {
    return (AccessFlag.PUBLIC.getBits() & accessFlag) > 0;
  }

  public boolean isPrivate() {
    return (AccessFlag.PRIVATE.getBits() & accessFlag) > 0;
  }

  public boolean isStatic() {
    return (AccessFlag.STATIC.getBits() & accessFlag) > 0;
  }

  public boolean isAbstract() {
    return (AccessFlag.ABSTRACT.getBits() & accessFlag) > 0;
  }

  public boolean isNative() {
    return (AccessFlag.NATIVE.getBits() & accessFlag) > 0;
  }

  public boolean isClinit() {
    return isStatic() && name.equals(CLINIT_METHOD_NAME);
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

  public String getClassBinaryName() {
    return methods.getRawClass().getBinaryName();
  }

  public enum AccessFlag {
    PUBLIC((short) 0x0001), // 0b00000000 00000001
    PRIVATE((short) 0x0002), // 0b00000000 00000010
    PROTECTED((short) 0x0004), // 0b00000000 00000100
    STATIC((short) 0x0008), // 0b00000000 00001000
    FINAL((short) 0x0010), // 0b00000000 00010000
    SYNCHRONIZED((short) 0x0020), // 0b00000000 00100000
    BRIDGE((short) 0x0040), // 0b00000000 01000000
    VARARGS((short) 0x0080), // 0b00000000 10000000
    NATIVE((short) 0x0100), // 0b00000001 00000000
    ABSTRACT((short) 0x0400), // 0b00000100 00000000
    STRICT((short) 0x0800), // 0b00001000 00000000
    // In a class file whose major version number is at least 46 and at most 60: Declared strictfp.
    SYNTHETIC((short) 0x1000); // 0b00010000 00000000

    private final short bits;

    AccessFlag(short bits) {
      this.bits = bits;
    }

    public short getBits() {
      return bits;
    }
  }
}
