package dev.ishikawa.lovejvm.rawclass.method.exceptionhandler;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;

public class ExceptionInfo {
  private final int startPc;
  private final int endPc;
  private final int
      handlerPc; // this value is a virtual exception handler = where to jump to handle the
  // exception
  private final ConstantClass catchType;

  public ExceptionInfo(int startPc, int endPc, int handlerPc, ConstantClass catchType) {
    this.startPc = startPc;
    this.endPc = endPc;
    this.handlerPc = handlerPc;
    this.catchType = catchType;
  }

  public boolean shouldCatch(String exceptionClassBinaryName, int pc) {
    return this.getCatchType().getName().getLabel().equals(exceptionClassBinaryName)
        && startPc <= pc
        && pc < endPc;
  }

  public int getHandlerPc() {
    return handlerPc;
  }

  public ConstantClass getCatchType() {
    return catchType;
  }

  public int size() {
    return 2 // startPc        = 2 bytes
        + 2 // endPc          = 2 bytes
        + 2 // handlerPc      = 2 bytes
        + 2; // catchTypeIndex = 2 bytes
  }
}
