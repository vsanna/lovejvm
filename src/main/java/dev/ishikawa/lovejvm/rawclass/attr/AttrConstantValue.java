package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

/**
 * only field can have this attr data part's binary format: u2 constantvalue_index ... must be
 * CONSTANT_{Integer,Float,Long,Double,String}
 */

// REFACTOR: add more limitation on the type param
public class AttrConstantValue extends Attr<ConstantPoolEntry> {
  public AttrConstantValue(ConstantUtf8 attrName, int dataLength, ConstantPoolEntry attrBody) {
    super(attrName, dataLength, attrBody);
    validate(attrBody);
  }

  private void validate(ConstantPoolEntry entry) {
    // REFACTOR: check the entry is in a whitelist.
  }
}
