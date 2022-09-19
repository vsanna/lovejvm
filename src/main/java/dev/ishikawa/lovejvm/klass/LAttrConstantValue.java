package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;

/**
 * only field can have this attr
 * */

// TODO: add more limitation on the type param
public class LAttrConstantValue extends LAttr<ConstantPoolEntry> {
    public LAttrConstantValue(
            ConstantUtf8 attrName,
            int dataLength,
            ConstantPoolEntry attrBody
    ) {
        super(attrName, dataLength, attrBody);
        validate(attrBody);
    }

    private void validate(ConstantPoolEntry entry) {
        // TODO: check the entry is in a whitelist.
    }
}
