package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods.LAttrBootstrapMethodsBody;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodHandle;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;

/** only method can have this attr */
public class AttrBootstrapMethods extends Attr<LAttrBootstrapMethodsBody> {
  public AttrBootstrapMethods(
      ConstantUtf8 attrName, int dataLength, LAttrBootstrapMethodsBody body) {
    super(attrName, dataLength, body);
  }

  public static class LAttrBootstrapMethodsBody {
    private short numBootstrapMethods;
    private List<LAttrBootstrapMethod> bootstrapMethods;

    public LAttrBootstrapMethodsBody(
        short numBootstrapMethods, List<LAttrBootstrapMethod> bootstrapMethods) {
      this.numBootstrapMethods = numBootstrapMethods;
      this.bootstrapMethods = bootstrapMethods;
    }

    public static class LAttrBootstrapMethod {
      private ConstantMethodHandle bootstrapMethodRef;
      private short numBootstrapArguments;
      private List<ConstantPoolEntry> bootstrapArguments;

      public LAttrBootstrapMethod(
          ConstantMethodHandle bootstrapMethodRef,
          short numBootstrapArguments,
          List<ConstantPoolEntry> bootstrapArguments) {
        this.bootstrapMethodRef = bootstrapMethodRef;
        this.numBootstrapArguments = numBootstrapArguments;
        this.bootstrapArguments = bootstrapArguments;
      }
    }
  }
}
