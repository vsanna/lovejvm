package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods.LAttrBootstrapMethodsBody;
import dev.ishikawa.lovejvm.rawclass.attr.AttrEnclosingMethod.AttrEnclosingMethodBody;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodHandle;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;

/** only method can have this attr */
public class AttrEnclosingMethod extends Attr<AttrEnclosingMethodBody> {
  public AttrEnclosingMethod(
      ConstantUtf8 attrName, int dataLength, AttrEnclosingMethodBody body) {
    super(attrName, dataLength, body);
  }

  public static class AttrEnclosingMethodBody {
    private final short classIndex;
    private final short methodIndex;

    public AttrEnclosingMethodBody(short classIndex, short methodIndex) {
      this.classIndex = classIndex;
      this.methodIndex = methodIndex;
    }
  }
}
