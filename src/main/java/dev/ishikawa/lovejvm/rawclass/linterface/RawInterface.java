package dev.ishikawa.lovejvm.rawclass.linterface;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;

public class RawInterface {
  private int accessFlag;
  private ConstantClass constantClassRef;

  public RawInterface(int accessFlag, ConstantClass constantClassRef) {
    this.accessFlag = accessFlag;
    this.constantClassRef = constantClassRef;
  }

  public int size() {
    return 2;
  }
}
