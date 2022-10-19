package java.util;

public final class Objects {
  private Objects() {}

  public static boolean equals(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
  }

  public static int hashCode(Object o) {
    return o != null ? o.hashCode() : 0;
  }

  public static <T> T requireNonNull(T obj) {
    if (obj == null) {
      throw new NullPointerException("given object is null");
    }
    return obj;
  }

  public static <T> T requireNonNull(T obj, String message) {
    if (obj == null) {
      throw new NullPointerException(message);
    }
    return obj;
  }

  public static <T> T requireNonNullElse(T obj, T defaultObj) {
    return (obj != null) ? obj : requireNonNull(defaultObj, "defaultObj");
  }
}
