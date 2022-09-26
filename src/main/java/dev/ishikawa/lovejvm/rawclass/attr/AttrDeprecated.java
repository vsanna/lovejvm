package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

/** only method can have this attr */
public class AttrDeprecated extends Attr<Void> {
  public AttrDeprecated(ConstantUtf8 attrName, int dataLength) {
    super(attrName, dataLength, null);
  }
}
