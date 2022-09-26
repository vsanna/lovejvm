package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.attr.AttrStackMapTable.LAttrStackMapBody;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;

/** only method can have this attr */
public class AttrStackMapTable extends Attr<LAttrStackMapBody> {
  public AttrStackMapTable(ConstantUtf8 attrName, int dataLength, LAttrStackMapBody stackMapBody) {
    super(attrName, dataLength, stackMapBody);
  }

  public static class LAttrStackMapBody {
    // TODO
    private short numberOfEntries;
    private List<LAttrStackMapEntry> stackMapFrame;

    public static class LAttrStackMapEntry {}
  }
}
