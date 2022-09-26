package dev.ishikawa.lovejvm.rawclass.method.exceptionhandler;

import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import org.jetbrains.annotations.Nullable;

public class RawExceptionHandler {
  private int startPc;
  private int endPc;
  private int handlerPc;
  @Nullable private ConstantClass catchType;

  public RawExceptionHandler(int startPc, int endPc, int handlerPc,
      ConstantClass catchType) {
    this.startPc = startPc;
    this.endPc = endPc;
    this.handlerPc = handlerPc;
    this.catchType = catchType;
  }

  public int size() {
    return 2 // startPc        = 2 bytes
        + 2  // endPc          = 2 bytes
        + 2  // handlerPc      = 2 bytes
        + 2; // catchTypeIndex = 2 bytes
  }
}
