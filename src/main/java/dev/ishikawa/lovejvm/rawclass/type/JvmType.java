package dev.ishikawa.lovejvm.rawclass.type;


import dev.ishikawa.lovejvm.vm.Word;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

/**
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.3">Primitive
 *     Types and Values</a>
 */
public enum JvmType {
  // Primitive Types
  BYTE(32, "byte", "B", DefaultValue.of(0), "java/lang/Byte"), // 1 byte
  SHORT(32, "short", "S", DefaultValue.of(0), "java/lang/Short"), // 2 byte
  CHAR(32, "char", "C", DefaultValue.of(0), "java/lang/Character"), // 2 byte
  INT(32, "int", "I", DefaultValue.of(0), "java/lang/Integer"), // 4 byte
  LONG(64, "long", "J", DefaultValue.of(0), "java/lang/Long"), // 8 byte
  FLOAT(32, "float", "F", DefaultValue.of(0.0F), "java/lang/Float"),
  DOUBLE(64, "double", "D", DefaultValue.of(0.0D), "java/lang/Double"),
  BOOLEAN(32, "boolean", "Z", DefaultValue.of(false), "java/lang/Boolean"),
  VOID(32, "void", "V", DefaultValue.of(null), "java/lang/Void"),

  //  RETURN_ADDRESS(32, null, null, DefaultValue.of(null), null), // address to the opcodes
  // Reference Types
  ARRAY(32, null, "[", DefaultValue.of(null), null),
  OBJECT_REFERENCE(32, null, "L", DefaultValue.of(null), null);

  /**
   * size means how many bytes is need to store the value in mem. size should be a number of
   * multiplied words (4, 8, 12, ...)
   */
  private final int bitSize;

  @Nullable final String primitiveName;

  private final DefaultValue defaultValue;

  @Nullable private final String jvmSignature;

  @Nullable private final String boxTypeBinaryName;

  JvmType(
      int bitSize,
      @Nullable String primitiveName,
      @Nullable String jvmSignature,
      DefaultValue defaultValue,
      @Nullable String boxTypeBinaryName) {
    this.primitiveName = primitiveName;
    this.defaultValue = defaultValue;
    this.jvmSignature = jvmSignature;
    this.boxTypeBinaryName = boxTypeBinaryName;
    assert bitSize % Word.BITS_SIZE == 0;
    this.bitSize = bitSize;
  }

  public int getBitSize() {
    return bitSize;
  }

  public @Nullable String getPrimitiveName() {
    return primitiveName;
  }

  public @Nullable String getJvmSignature() {
    return jvmSignature;
  }

  public @Nullable String getBoxTypeBinaryName() {
    return boxTypeBinaryName;
  }

  public int wordSize() {
    return getBitSize() / Word.BITS_SIZE;
  }

  /** entryMap is map of jvm's signature to Java's data ex: Z -> BOOLEAN, */
  private static final Map<String, JvmType> entryMap =
      Arrays.stream(JvmType.values())
          .filter((it) -> !Objects.isNull(it.getJvmSignature()))
          .collect(Collectors.toMap(JvmType::getJvmSignature, Function.identity()));

  /** REFACTOR: check OBJECT_REFERENCE is ok as a default value or not. */
  public static JvmType findByJvmSignature(String jvmSignature) {
    return Optional.ofNullable(entryMap.get(jvmSignature)).orElse(OBJECT_REFERENCE);
  }

  private static class DefaultValue<T> {
    private final T value;

    public DefaultValue(T value) {
      this.value = value;
    }

    public T getValue() {
      return value;
    }

    private static <S> DefaultValue of(S value) {
      return new DefaultValue<S>(value);
    }
  }

  public static final Set<JvmType> primaryTypes =
      Set.of(BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR, VOID);
}
