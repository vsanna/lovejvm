package dev.ishikawa.lovejvm.rawclass.field;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fields is a container of some class's fields. Fields also manages where to put words according to
 * the field to put the data into.
 */
public class Fields {
  private RawClass rawClass;
  private final int entrySize;
  private final List<RawField> fields;

  public Fields(int entrySize, List<RawField> fields) {
    if (entrySize != fields.size())
      throw new RuntimeException("invalid Fields. the size doesn't match with num of entries");
    this.entrySize = entrySize;
    this.fields = fields;
  }

  public int getEntrySize() {
    return entrySize;
  }

  public Optional<RawField> findAllBy(String fieldName) {
    return fields.stream().filter((f) -> f.hasSignature(fieldName)).findFirst();
  }

  public Optional<RawField> findMemberBy(String fieldName) {
    return fields.stream()
        .filter((f) -> !f.isStatic())
        .filter((f) -> f.hasSignature(fieldName))
        .findFirst();
  }

  public Optional<RawField> findStaticBy(String fieldName) {
    return fields.stream()
        .filter(RawField::isStatic)
        .filter((field) -> field.hasSignature(fieldName))
        .findFirst();
  }

  /** @return total size(words) of necessary binary for fields of this class's object */
  public int getMemberFieldsMemWords() {
    return fields.stream()
        .filter((field) -> !field.isStatic())
        .map((field) -> JvmType.findByJvmSignature(field.getDescriptor().getLabel()).wordSize())
        .reduce(0, Integer::sum);
  }

  /** @return total size(words) of necessary binary for static fields of this class */
  public int getStaticFieldsMemWords() {
    return fields.stream()
        .filter(RawField::isStatic)
        .map((field) -> JvmType.findByJvmSignature(field.getDescriptor().getLabel()).wordSize())
        .reduce(0, Integer::sum);
  }

  public List<RawField> getStaticFields() {
    return fields.stream().filter(RawField::isStatic).collect(Collectors.toList());
  }

  public List<RawField> getMemberFields() {
    return fields.stream().filter(it -> !it.isStatic()).collect(Collectors.toList());
  }

  public int size() {
    return 2 + fields.stream().map(RawField::size).reduce(0, Integer::sum);
  }

  public RawClass getRawClass() {
    return rawClass;
  }

  public void setRawClass(RawClass rawClass) {
    this.rawClass = rawClass;
  }

  /**
   * offsetToField returns offset in HEAP area (Not MethodArea)
   *
   * @return num of bytes
   */
  public int offsetToMemberFieldBytes(RawField targetField) {
    if (!getMemberFields().contains(targetField)) return 0;

    int result = 1 * Word.BYTES_SIZE; // reference to parent object

    for (RawField memberField : getMemberFields()) {
      if (targetField != memberField) {
        result += memberField.getJvmType().wordSize() * Word.BYTES_SIZE;
      } else {
        break;
      }
    }

    return result;
  }
}
