package dev.ishikawa.lovejvm.memory.methodarea;


import java.util.Arrays;

/**
 * MethodAreaSimulator simulate MethodArea by utilizing host java's features. This doesn't manage
 * native memory at all.
 */
class MethodAreaSimulator implements MethodArea {
  private byte[] memory = new byte[100000];
  private int size = 0;

  private MethodAreaSimulator() {}

  @Override
  public byte lookupByte(int address) {
    return memory[address];
  }

  @Override
  public byte[] retrieve(int startingAddress, int size) {
    byte[] result = new byte[size];
    System.arraycopy(memory, startingAddress, result, 0, size);
    return result;
  }

  @Override
  public void save(int startingAddress, byte[] bytes) {
    System.arraycopy(bytes, 0, memory, startingAddress, bytes.length);
    // size is not updated here.
    // So DO NOT use sae when allocating new mem space for a new object
  }

  @Override
  public void allocate(byte[] bytes) {
    System.arraycopy(bytes, 0, memory, size, bytes.length);
    size += bytes.length;
  }

  @Override
  public int headAddress() {
    return size;
  }

  @Override
  public void clear(int startingAddress, int staticAreaByteSize) {
    var zeroByteArray = new byte[staticAreaByteSize];
    Arrays.fill(zeroByteArray, (byte) 0);
    System.arraycopy(zeroByteArray, 0, memory, startingAddress, staticAreaByteSize);
  }

  public static final MethodArea INSTANCE = new MethodAreaSimulator();
}
