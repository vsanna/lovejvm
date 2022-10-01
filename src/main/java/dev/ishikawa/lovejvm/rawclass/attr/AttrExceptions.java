package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.attr.AttrExceptions.LAttrExceptionsBody;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import java.util.List;
import java.util.Optional;

/** only method can have this attr */
public class AttrExceptions extends Attr<LAttrExceptionsBody> {
  public AttrExceptions(ConstantUtf8 attrName, int dataLength, LAttrExceptionsBody exceptionsBody) {
    super(attrName, dataLength, exceptionsBody);
  }

  public static class LAttrExceptionsBody {
    private final short numberOfExceptions;
    private final List<ConstantClass> exceptionClasses;

    public List<ConstantClass> getExceptionClasses() {
      return exceptionClasses;
    }

    public Optional<ConstantClass> findBy(String binaryName) {
      return getExceptionClasses().stream()
          .filter(it -> it.getName().getLabel().equals(binaryName))
          .findFirst();
    }

    public LAttrExceptionsBody(short numberOfExceptions, List<ConstantClass> exceptionClasses) {
      this.numberOfExceptions = numberOfExceptions;
      this.exceptionClasses = exceptionClasses;
    }
  }
}
