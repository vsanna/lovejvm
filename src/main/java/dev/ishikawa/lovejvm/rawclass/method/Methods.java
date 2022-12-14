package dev.ishikawa.lovejvm.rawclass.method;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import java.util.List;
import java.util.Optional;

public class Methods {
  private RawClass rawClass;
  private final int entrySize;
  private List<RawMethod> methods;

  public Methods(int entrySize, List<RawMethod> methods) {
    if (entrySize != methods.size())
      throw new RuntimeException(
          "invalid Methods. the entrySize doesn't match with num of entries");
    this.entrySize = entrySize;
    this.methods = methods;
    this.methods.forEach(it -> it.setMethods(this));
  }

  public Optional<RawMethod> findAllBy(String methodName, String methodDescriptor) {
    return methods.stream()
        .filter((method) -> method.hasSignature(methodName, methodDescriptor))
        .findFirst();
  }

  public Optional<RawMethod> findMemberBy(String methodName, String methodDescriptor) {
    return methods.stream()
        .filter((method) -> !method.isStatic())
        .filter((method) -> method.hasSignature(methodName, methodDescriptor))
        .findFirst();
  }

  public Optional<RawMethod> findStaticBy(String methodName, String methodDescriptor) {
    return methods.stream()
        .filter(RawMethod::isStatic)
        .filter((method) -> method.hasSignature(methodName, methodDescriptor))
        .findFirst();
  }

  public int size() {
    return 2 // num of methods(2B)
        + methods.stream().map(RawMethod::size).reduce(0, Integer::sum);
  }

  /**
   * offstToField returns offset in METHODAREA area (Not Heap) This is different in that point from
   * Fields#offsetToField
   *
   * @return num of bytes
   */
  public int offsetToMethod(RawMethod method) {
    if (!methods.contains(method)) return 0;

    int result = 2;

    for (RawMethod rawMethod : methods) {
      if (rawMethod != method) {
        result += rawMethod.size();
      } else {
        break;
      }
    }

    return result;
  }

  public RawClass getRawClass() {
    return rawClass;
  }

  public void setRawClass(RawClass rawClass) {
    this.rawClass = rawClass;
  }

  public int getEntrySize() {
    return entrySize;
  }
}
