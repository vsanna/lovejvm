package dev.ishikawa.lovejvm.rawclass.method;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.attr.AttrName;
import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.attr.AttrCode;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

import java.util.Objects;
import java.util.Optional;

public class RawMethod {
    private Methods methods;
    private int accessFlag;
    private ConstantUtf8 name;
    private ConstantUtf8 descriptor;
    private Attrs attrs;

    public RawMethod(
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
        return getCode().map(AttrCode::getOperandStackSize).orElse(0);
    }

    public int getLocalsSize() {
        return getCode().map(AttrCode::getLocalsSize).orElse(0);
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
    private Optional<AttrCode> getCode() {
        return attrs.findAllBy(AttrName.CODE).stream().map((it) -> (AttrCode)it).findFirst();
    }

    public int size() {
        return offsetToAttrs()
                + attrs.size(); // attrs
    }

    public int offsetToAttrs() {
        return 2     // accessFlag
                + 2  // methodName
                + 2; // methodDesc
    }

    public void setMethods(Methods methods) {
        this.methods = methods;
    }

    public RawClass getKlass() {
        return this.methods.getRawClass();
    }

    public boolean hasSignature(String methodName, String methodDescriptor) {
        return Objects.equals(this.getName().getLabel(), methodName)
                && Objects.equals(this.getDescriptor().getLabel(), methodDescriptor);
    }
}
