package java.lang.invoke;

abstract public class CallSite {
  CallSite(MethodType type) {}

  CallSite(MethodHandle target) {}

  CallSite(MethodType targetType, MethodHandle createTargetHook) throws Throwable {}
}
