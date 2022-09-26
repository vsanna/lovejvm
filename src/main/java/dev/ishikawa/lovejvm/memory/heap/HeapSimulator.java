package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/**
 * HeapSimulator simulate Heap by utilizing host java's features. This doesn't manage native memory
 * at all.
 *
 * <p>NOTE: At this mement, no free, no GC!
 */
class HeapSimulator implements Heap {
  private byte[] memory = new byte[100000];
  private int size = 0;

  private HeapSimulator() {}

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

  public static final Heap INSTANCE = new HeapSimulator();
}
