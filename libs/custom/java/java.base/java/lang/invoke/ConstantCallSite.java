package java.lang.invoke;

public class ConstantCallSite extends CallSite {
    public ConstantCallSite(MethodType type) {
        super(type);
    }

    protected ConstantCallSite(MethodType targetType, MethodHandle createTargetHook) throws Throwable {
        super(targetType, createTargetHook);
    }

    public MethodHandle getTarget() {
        return null;
    }

    public void setTarget(MethodHandle newTarget) {}

    public MethodHandle dynamicInvoker() {
        return null;
    }
}
