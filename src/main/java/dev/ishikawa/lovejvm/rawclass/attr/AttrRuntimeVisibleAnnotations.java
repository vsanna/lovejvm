package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;

/** only method can have this attr */
public class AttrRuntimeVisibleAnnotations extends Attr<List<AttrAnnotation>> {
  public AttrRuntimeVisibleAnnotations(
      ConstantUtf8 attrName, int dataLength, List<AttrAnnotation> entries) {
    super(attrName, dataLength, entries);
  }
}
