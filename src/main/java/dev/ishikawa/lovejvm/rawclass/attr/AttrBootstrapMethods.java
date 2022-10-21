package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.attr.AttrBootstrapMethods.AttrBootstrapMethodsBody;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantMethodHandle;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantPoolEntry;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;

/** only method can have this attr */
public class AttrBootstrapMethods extends Attr<AttrBootstrapMethodsBody> {
  public AttrBootstrapMethods(
      ConstantUtf8 attrName, int dataLength, AttrBootstrapMethodsBody body) {
    super(attrName, dataLength, body);
  }

  public static class AttrBootstrapMethodsBody {
    private final short numBootstrapMethods;
    private final List<AttrBootstrapMethod> bootstrapMethods;

    public AttrBootstrapMethodsBody(
        short numBootstrapMethods, List<AttrBootstrapMethod> bootstrapMethods) {
      this.numBootstrapMethods = numBootstrapMethods;
      this.bootstrapMethods = bootstrapMethods;
    }

    public AttrBootstrapMethod findBy(int index) {
      return this.bootstrapMethods.get(index);
    }

    public static class AttrBootstrapMethod {
      private final ConstantMethodHandle bootstrapMethodRef;
      private final short numBootstrapArguments;
      private final List<ConstantPoolEntry> bootstrapArguments;

      public AttrBootstrapMethod(
          ConstantMethodHandle bootstrapMethodRef,
          short numBootstrapArguments,
          List<ConstantPoolEntry> bootstrapArguments) {
        this.bootstrapMethodRef = bootstrapMethodRef;
        this.numBootstrapArguments = numBootstrapArguments;
        this.bootstrapArguments = bootstrapArguments;
      }

      public ConstantMethodHandle getBootstrapMethodRef() {
        return bootstrapMethodRef;
      }

      public List<ConstantPoolEntry> getBootstrapArguments() {
        return bootstrapArguments;
      }
    }
  }
}
