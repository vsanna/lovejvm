package dev.ishikawa.lovejvm.rawclass.linterface;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;

public class RawInterface {
  private final ConstantClass constantClassRef;

  public RawInterface(ConstantClass constantClassRef) {
    this.constantClassRef = constantClassRef;
  }

  public int size() {
    return 2; // 2 byte as index in constant pool
  }

  public ConstantClass getConstantClassRef() {
    return constantClassRef;
  }
}
