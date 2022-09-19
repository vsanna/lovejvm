package dev.ishikawa.lovejvm.klass;

import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;

import java.util.List;

/**
 * only method can have this attr
 * */

public class LAttrLineNumberTable extends LAttr<List<LAttrLineNumberTable.LAttrLineNumberTableEntry>> {
    public LAttrLineNumberTable(
            ConstantUtf8 attrName,
            int dataLength,
            List<LAttrLineNumberTableEntry> entries
    ) {
        super(attrName, dataLength, entries);
    }

    public static class LAttrLineNumberTableEntry {
        private int instructionLine;
        private int originalLine;

        public LAttrLineNumberTableEntry(int instructionLine, int originalLine) {
            this.instructionLine = instructionLine;
            this.originalLine = originalLine;
        }
    }
}
