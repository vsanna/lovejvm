package java.lang;

public abstract class ClassLoader {
    private final ClassLoader parent;
    private final String name;
    private final String nameAndId;

    public ClassLoader(ClassLoader parent, String name, String nameAndId, Object assertionLock) {
        this.parent = parent;
        this.name = name;
        this.nameAndId = nameAndId;
    }

}
