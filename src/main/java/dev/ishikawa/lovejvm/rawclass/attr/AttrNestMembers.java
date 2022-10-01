package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.attr.AttrNestMembers.LAttrNestMembersBody;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;

/** only method can have this attr */
public class AttrNestMembers extends Attr<LAttrNestMembersBody> {
  public AttrNestMembers(ConstantUtf8 attrName, int dataLength, LAttrNestMembersBody body) {
    super(attrName, dataLength, body);
  }

  public static class LAttrNestMembersBody {
    private final short numberOfClasses;
    private final List<ConstantClass> classes;

    public LAttrNestMembersBody(short numberOfClasses, List<ConstantClass> classes) {
      this.numberOfClasses = numberOfClasses;
      this.classes = classes;
    }
  }
}
