package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * HeapSimulator simulate Heap by utilizing host java's features.
 * This doesn't manage native memory at all.
 *
 * NOTE: At this mement, no free, no GC!
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
  public void setValue(int address, List<Word> value) {
    int head = address;
    for (Word word : value) {
      System.arraycopy(word.getBytes(), 0, memory, head, word.getBytes().length);
      head += word.getBytes().length;
    }
  }

  @Override
  public int headAddress() {
    return size;
  }

  public static final Heap INSTANCE = new HeapSimulator();
}
