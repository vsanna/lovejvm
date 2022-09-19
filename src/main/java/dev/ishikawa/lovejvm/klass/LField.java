package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.Attrs;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;

public class LField {
    private int accessFlag;
    private ConstantUtf8 name;
    private ConstantUtf8 descriptor;
    private Attrs attrs;

    public LField(
            int accessFlag,
            ConstantUtf8 name,
            ConstantUtf8 descriptor,
            Attrs attrs) {
        this.accessFlag = accessFlag;
        this.name = name;
        this.descriptor = descriptor;
        this.attrs = attrs;
    }

    public int size() {
        return 2 // accessFlag
                + 2 // fieldName
                + 2 // fieldDesc
                + attrs.size(); // attrs
    }
}
