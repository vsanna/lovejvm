package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

/** DBEUG this is only for debugging. */
public class AttrDummy extends Attr<String> {
  public AttrDummy(ConstantUtf8 attrName, int dataLength, String body) {
    super(attrName, dataLength, body);
  }
}
