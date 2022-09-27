package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;

/** only method can have this attr */
public class AttrRuntimeInvisibleAnnotations extends Attr<List<LAttrAnnotation>> {
  public AttrRuntimeInvisibleAnnotations(
      ConstantUtf8 attrName, int dataLength, List<LAttrAnnotation> entries) {
    super(attrName, dataLength, entries);
  }
}
