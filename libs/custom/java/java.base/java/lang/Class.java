package java.lang;

import java.util.Map;

public final class Class<T> {
    private final ClassLoader classLoader;
    private final Class<?> componentType;
    private transient String name;

    private Class(ClassLoader loader, Class<?> arrayComponentType) {
        classLoader = loader;
        componentType = arrayComponentType;
    }

    public String getName() {
        String name = this.name;
        return name != null ? name : initClassName();
    }

    // TODO: this should be native
    private String initClassName() {
        return "dummy";
    }

    public native Class<? super T> getSuperclass();

    Map<String, T> enumConstantDirectory() {
        throw new UnsupportedOperationException("");
    }

    public String getCanonicalName() {
        throw new UnsupportedOperationException("");
    }

    static native Class<?> getPrimitiveClass(String name);

    public native boolean isAssignableFrom(Class<?> cls);

}
