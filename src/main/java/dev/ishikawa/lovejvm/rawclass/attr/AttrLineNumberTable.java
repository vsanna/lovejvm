package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;

/** only method can have this attr */
public class AttrLineNumberTable extends Attr<List<AttrLineNumberTable.LAttrLineNumberTableEntry>> {
  public AttrLineNumberTable(
      ConstantUtf8 attrName, int dataLength, List<LAttrLineNumberTableEntry> entries) {
    super(attrName, dataLength, entries);
  }

  public static class LAttrLineNumberTableEntry {
    private final int instructionLine;
    private final int originalLine;

    public LAttrLineNumberTableEntry(int instructionLine, int originalLine) {
      this.instructionLine = instructionLine;
      this.originalLine = originalLine;
    }
  }
}
