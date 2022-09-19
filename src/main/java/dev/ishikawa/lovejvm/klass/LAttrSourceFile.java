package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;

import java.util.List;

/**
 * only method can have this attr
 * */

public class LAttrSourceFile extends LAttr<ConstantPoolEntry> {
    public LAttrSourceFile(
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

    public String getSourceFileName() {
        ConstantUtf8 sourceFileNameEntry = (ConstantUtf8) getAttrBody();
        return sourceFileNameEntry.getLabel();
    }
}
