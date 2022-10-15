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
  BYTE(32, "B", DefaultValue.of(0)), // 1 byte
  SHORT(32, "S", DefaultValue.of(0)), // 2 byte
  CHAR(32, "C", DefaultValue.of(0)), // 2 byte
  INT(32, "I", DefaultValue.of(0)), // 4 byte
  LONG(64, "J", DefaultValue.of(0)), // 8 byte
  FLOAT(32, "F", DefaultValue.of(0.0F)),
  DOUBLE(64, "D", DefaultValue.of(0.0D)),
  BOOLEAN(32, "Z", DefaultValue.of(false)),
  RETURN_ADDRESS(32, null, DefaultValue.of(null)), // address to the opcodes

  // Reference Types
  ARRAY(32, "[", DefaultValue.of(null)),
  OBJECT_REFERENCE(32, "L", DefaultValue.of(null)),

  VOID(32, "V", DefaultValue.of(null));

  /**
   * size means how many bytes is need to store the value in mem. size should be a number of
   * multiplied words (4, 8, 12, ...)
   */
  private final int size;

  private final DefaultValue defaultValue;

  @Nullable private final String jvmSignature;

  JvmType(int size, @Nullable String jvmSignature, DefaultValue defaultValue) {
    this.defaultValue = defaultValue;
    this.jvmSignature = jvmSignature;
    assert size % Word.BITS_SIZE == 0;
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  public @Nullable String getJvmSignature() {
    return jvmSignature;
  }

  public int wordSize() {
    return getSize() / Word.BITS_SIZE;
  }

  /** entryMap is map of jvm's signature to Java's data ex: Z -> BOOLEAN, */
  private static Map<String, JvmType> entryMap =
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

  // when reference value(32bit) is 0, it is considered as null
  public static int NULL = 0;

  public static final Set<JvmType> primaryTypes = Set.of(BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, CHAR);
}
