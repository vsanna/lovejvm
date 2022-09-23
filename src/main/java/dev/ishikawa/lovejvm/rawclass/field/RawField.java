package dev.ishikawa.lovejvm.rawclass.field;

import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

public class RawField {
    private int accessFlag;
    private ConstantUtf8 name;
    private ConstantUtf8 descriptor;
    private Attrs attrs;

    public RawField(
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
