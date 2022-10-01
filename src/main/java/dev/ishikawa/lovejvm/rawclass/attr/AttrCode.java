package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.rawclass.method.exceptionhandler.ExceptionInfoTable;

/** only method can have this attr */
public class AttrCode extends Attr<AttrCode.LAttrCodeBody> {
  public AttrCode(ConstantUtf8 attrName, int dataLength, LAttrCodeBody body) {
    super(attrName, dataLength, body);
  }

  public static AttrCode of(
      ConstantUtf8 attrName,
      int dataLength,
      int operandStackSize,
      int localsSize,
      int instructionLength,
      ExceptionInfoTable exceptionInfoTable,
      Attrs attrs) {
    var body =
        new LAttrCodeBody(
            operandStackSize, localsSize, instructionLength, exceptionInfoTable, attrs);
    return new AttrCode(attrName, dataLength, body);
  }

  public int getOperandStackSize() {
    return this.getAttrBody().operandStackSize;
  }

  public int getLocalsSize() {
    return this.getAttrBody().localsSize;
  }

  public static class LAttrCodeBody {
    private final int operandStackSize;
    private final int localsSize;
    private final int codeLength;
    private final ExceptionInfoTable exceptionInfoTable;
    private final Attrs attrs;

    public LAttrCodeBody(
        int operandStackSize,
        int localsSize,
        int codeLength,
        ExceptionInfoTable exceptionInfoTable,
        Attrs attrs) {
      this.operandStackSize = operandStackSize;
      this.localsSize = localsSize;
      this.codeLength = codeLength;
      this.exceptionInfoTable = exceptionInfoTable;
      this.attrs = attrs;
    }

    public ExceptionInfoTable getExceptionInfoTable() {
      return exceptionInfoTable;
    }
  }
}
