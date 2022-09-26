package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.attr.AttrInnerClass.AttrInnerClassBody;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/** only method can have this attr */
public class AttrInnerClass extends Attr<AttrInnerClassBody> {
  public AttrInnerClass(ConstantUtf8 attrName, int dataLength, AttrInnerClassBody body) {
    super(attrName, dataLength, body);
  }

  public static class AttrInnerClassBody {
    private short numberOfClasses;
    private List<LAttrInnerClassEntry> classes;

    public AttrInnerClassBody(short numberOfClasses, List<LAttrInnerClassEntry> classes) {
      this.numberOfClasses = numberOfClasses;
      this.classes = classes;
    }

    public static class LAttrInnerClassEntry {
      private ConstantClass innerClassInfo;
      // when outerClassInfo is null, this class is top-level class, local class, or anonymous class
      private @Nullable ConstantClass outerClassInfo;
      // when innerName is null, this class is anonymous
      private @Nullable ConstantUtf8 innerName;
      private int accessFlag;

      public LAttrInnerClassEntry(
          ConstantClass innerClassInfo,
          @Nullable ConstantClass outerClassInfo,
          @Nullable ConstantUtf8 innerName,
          int accessFlag) {
        this.innerClassInfo = innerClassInfo;
        this.outerClassInfo = outerClassInfo;
        this.innerName = innerName;
        this.accessFlag = accessFlag;
      }
    }
  }
}
