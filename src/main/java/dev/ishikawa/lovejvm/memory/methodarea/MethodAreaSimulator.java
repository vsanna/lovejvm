package dev.ishikawa.lovejvm.memory.methodarea;


import java.util.Arrays;

/**
 * MethodAreaSimulator simulate MethodArea by utilizing host java's features. This doesn't manage
 * native memory at all.
 */
class MethodAreaSimulator implements MethodArea {
  private final byte[] memory = new byte[10 * 1000 * 1000]; // 10MB
  private int size = 0;

  MethodAreaSimulator() {}

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

    if (size < startingAddress + bytes.length) {
      size = startingAddress + bytes.length;
    }
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
}
