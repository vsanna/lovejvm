package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.rawclass.method.exceptionhandler.ExceptionHandlers;

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
      ExceptionHandlers exceptionHandlers,
      Attrs attrs) {
    var body =
        new LAttrCodeBody(
            operandStackSize, localsSize, instructionLength, exceptionHandlers, attrs);
    return new AttrCode(attrName, dataLength, body);
  }

  public int getOperandStackSize() {
    return this.getAttrBody().operandStackSize;
  }

  public int getLocalsSize() {
    return this.getAttrBody().localsSize;
  }

  static class LAttrCodeBody {
    private int operandStackSize;
    private int localsSize;
    private int instructionLength;
    private ExceptionHandlers exceptionHandlers;
    private Attrs attrs;

    public LAttrCodeBody(
        int operandStackSize,
        int localsSize,
        int instructionLength,
        ExceptionHandlers exceptionHandlers,
        Attrs attrs) {
      this.operandStackSize = operandStackSize;
      this.localsSize = localsSize;
      this.instructionLength = instructionLength;
      this.exceptionHandlers = exceptionHandlers;
      this.attrs = attrs;
    }
  }
}
