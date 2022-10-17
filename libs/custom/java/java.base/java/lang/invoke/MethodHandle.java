package java.lang.invoke;

public abstract class MethodHandle {
  MethodHandle viewAsType(MethodType newType, boolean strict) {
    throw new UnsupportedOperationException("");
  }
}
