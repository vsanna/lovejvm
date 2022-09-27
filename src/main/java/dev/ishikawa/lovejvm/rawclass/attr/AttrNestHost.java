package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

public class AttrNestHost extends Attr<ConstantClass> {
  public AttrNestHost(ConstantUtf8 attrName, int dataLength, ConstantClass attrBody) {
    super(attrName, dataLength, attrBody);
  }
}
