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
  public void allocate(byte[] bytes) {
    System.arraycopy(bytes, 0, memory, size, bytes.length);
  }

  @Override
  public int headAddress() {
    return size;
  }

  @Override
  public void clear(int startingAddress, int staticAreaByteSize) {
    var zeroByteArray = new byte[staticAreaByteSize];
    Arrays.fill(zeroByteArray, (byte)0);
    System.arraycopy(zeroByteArray, 0, memory, startingAddress, staticAreaByteSize);
  }

  static public final MethodArea INSTANCE = new MethodAreaSimulator();
}
