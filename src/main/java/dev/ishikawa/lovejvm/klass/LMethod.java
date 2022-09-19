package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.AttrName;
import dev.ishikawa.lovejvm.klass.constantpool.Attrs;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;

import java.util.Optional;

public class LMethod {
    private Methods methods;
    private int accessFlag;
    private ConstantUtf8 name;
    private ConstantUtf8 descriptor;
    private Attrs attrs;

    public LMethod(
            int accessFlag,
            ConstantUtf8 name,
            ConstantUtf8 descriptor,
            Attrs attrs) {
        this.accessFlag = accessFlag;
        this.name = name;
        this.descriptor = descriptor;
        this.attrs = attrs;
    }

    public ConstantUtf8 getName() {
        return name;
    }

    public ConstantUtf8 getDescriptor() {
        return descriptor;
    }

    public boolean isStatic() {
        return (0b1000 & accessFlag) > 0;
    }

    public int getOperandStackSize() {
        return getCode().map(LAttrCode::getOperandStackSize).orElse(0);
    }

    public int getLocalsSize() {
        return getCode().map(LAttrCode::getLocalsSize).orElse(0);
    }

    public Attrs getAttrs() {
        return attrs;
    }

    /**
     * If the method is either native or abstract, and is not a class or interface initialization method,
     *     then its method_info structure must not have a Code attribute in its attributes table.
     * Otherwise, its method_info structure must have exactly one Code attribute in its attributes table.
     *
     */
    private Optional<LAttrCode> getCode() {
        return attrs.findAllBy(AttrName.CODE).stream().map((it) -> (LAttrCode)it).findFirst();
    }

    public int size() {
        return 2 // accessFlag
                + 2 // methodName
                + 2 // methodDesc
                + attrs.size(); // attrs
    }

    public int offsetToAttrs() {
        return 2     // accessFlag
                + 2  // methodName
                + 2; // methodDesc
    }

    public Methods getMethods() {
        return methods;
    }

    public void setMethods(Methods methods) {
        this.methods = methods;
    }
}
