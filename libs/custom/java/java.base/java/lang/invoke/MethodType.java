package java.lang.invoke;


public class MethodType {
  Class<?>[] ptypes;
  Class<?>   rtype;

  public int parameterCount() {
    return ptypes.length;
  }

  public Class<?> returnType() {
    return rtype;
  }

  int parameterSlotCount() {
    throw new UnsupportedOperationException("");
  }
}
