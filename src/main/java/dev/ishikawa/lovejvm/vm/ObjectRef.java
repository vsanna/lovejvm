package dev.ishikawa.lovejvm.vm;

public class ObjectRef {
  private final int address;

  /** size means the length of mem bytes occupied by this object */
  private final int size;

  public ObjectRef(int address, int size) {
    this.address = address;
    this.size = size;
  }

  public int getAddress() {
    return address;
  }
}
