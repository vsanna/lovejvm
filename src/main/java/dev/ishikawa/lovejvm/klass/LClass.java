package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.Attrs;
import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantClass;

import java.util.Optional;

public class LClass {
    public LClass(
            byte[] raw,
            String fullyQualifiedName,
            String name,
            String filename,
            String classLoaderName,
            int minorVersion,
            int majorVersion,
            ConstantPool constantPool,
            int accessFlag,
            ConstantClass thisClass,
            ConstantClass superClass,
            Interfaces interfaces,
            Fields fileds,
            Methods methods,
            Attrs attrs) {
        this.raw = raw;
        this.fullyQualifiedName = fullyQualifiedName;
        this.name = name;
        this.filename = filename;
        this.classLoaderName = classLoaderName;
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.constantPool = constantPool;
        this.accessFlag = accessFlag;
        this.thisClass = thisClass;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.fileds = fileds;
        this.methods = methods;
        this.attrs = attrs;
    }

    // raw bytes of the classfile
    private final byte[] raw;
    private final String fullyQualifiedName;
    private final String name;
    private final String filename;
    private final String classLoaderName;

    private final int minorVersion;
    private final int majorVersion;

    private final ConstantPool constantPool;

    private final int accessFlag;

    private final ConstantClass thisClass;
    private final ConstantClass superClass;

    private final Interfaces interfaces;

    private final Fields fileds;

    private final Methods methods;

    private final Attrs attrs;

    public byte[] getRaw() {
        return raw;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getName() {
        return name;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public int getAccessFlag() {
        return accessFlag;
    }

    public ConstantClass getThisClass() {
        return thisClass;
    }

    public ConstantClass getSuperClass() {
        return superClass;
    }

    public Interfaces getInterfaces() {
        return interfaces;
    }

    public Fields getFileds() {
        return fileds;
    }

    public Methods getMethods() {
        return methods;
    }

    public Attrs getAttrs() {
        return attrs;
    }

    public String getClassLoaderName() {
        return classLoaderName;
    }

    public Optional<LMethod> findEntryPoint() {
        // TODO: 一旦mainでないもの
        return methods.findStaticBy("add", "()V");
    }
}