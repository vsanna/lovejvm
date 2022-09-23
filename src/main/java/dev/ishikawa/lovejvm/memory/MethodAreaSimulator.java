package dev.ishikawa.lovejvm.memory;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodref;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantNameAndType;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * MethodAreaSimulator simulate MethodArea by utilizing host java's features. This doesn't manage
 * native memory at all.
 */
public class MethodAreaSimulator implements MethodArea {
  private Map<String, ClassEntry> classMap = new HashMap<>();
  private byte[] memory = new byte[100000];
  private int size = 0;

  @Override
  public byte lookupByte(int pc) {
    return memory[pc];
  }

  @Override
  public int lookupCodeSectionAddress(RawMethod method) {
    RawClass rawClass = method.getKlass();
    var classAddr =
        Optional.ofNullable(classMap.get(rawClass.getName()))
            .map(it -> it.address)
            .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));

    var offsetToMethods = rawClass.offsetToMethods();
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
  public void register(RawClass rawClass) {
    // TODO: According to the reference, The same name class can be loaded under different
    // classloaders.
    // Take that into consideration.
    classMap.put(rawClass.getName(), new ClassEntry(size, rawClass));
    System.arraycopy(rawClass.getRaw(), 0, memory, size, rawClass.getRaw().length);
    size = rawClass.getRaw().length;
  }

  @Override
  public RawMethod lookupMethod(ConstantMethodref constantMethodref) {
    ConstantClass constantClassRef = constantMethodref.getConstantClassRef();
    RawClass lClass = lookupClass(constantClassRef);
    ConstantNameAndType nameAndTypeRef = constantMethodref.getNameAndType();
    return lClass
        .findBy(nameAndTypeRef)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

  private RawClass lookupClass(ConstantClass constantClass) {
    return Optional.ofNullable(classMap.get(constantClass.getName().getLabel()))
        .map(it -> it.rawClass)
        .orElseThrow(() -> new RuntimeException("Non existing class is tried to load"));
  }

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
