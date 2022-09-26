package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

/** only method can have this attr */
public class AttrSignature extends Attr<Short> {
  public AttrSignature(ConstantUtf8 attrName, int dataLength, short signatureIndex) {
    super(attrName, dataLength, signatureIndex);
  }
}
