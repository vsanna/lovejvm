package dev.ishikawa.lovejvm.memory.heap;

/**
 * HeapSimulator simulate Heap by utilizing byte array.
 * This doesn't manage native memory at all.
 *
 * NOTE: At this moment, no GC!
 */
class HeapSimulator implements Heap {
  private final byte[] memory = new byte[10 * 1000 * 1000]; // 10MB
  private int size = 0;

  @Override
  public void allocate(byte[] bytes) {
    System.arraycopy(bytes, 0, memory, size, bytes.length);
    size += bytes.length;
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
  }

  @Override
  public int headAddress() {
    return size;
  }
}
