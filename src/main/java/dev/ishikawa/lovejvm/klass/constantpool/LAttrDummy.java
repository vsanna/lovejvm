package dev.ishikawa.lovejvm.klass.constantpool;

import dev.ishikawa.lovejvm.klass.ExceptionHandlers;
import dev.ishikawa.lovejvm.klass.LAttr;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;

/**
 * DBEUG
 * this is only for debugging.
 * */

public class LAttrDummy extends LAttr<String> {
    public LAttrDummy(
            ConstantUtf8 attrName,
            int dataLength,
            String body
    ) {
        super(attrName, dataLength, body);
    }
}
