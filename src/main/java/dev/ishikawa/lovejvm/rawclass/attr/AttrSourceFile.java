package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

/** only method can have this attr */
public class AttrSourceFile extends Attr<ConstantPoolEntry> {
  public AttrSourceFile(ConstantUtf8 attrName, int dataLength, ConstantPoolEntry attrBody) {
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
