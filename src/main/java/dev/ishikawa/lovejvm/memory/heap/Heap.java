package dev.ishikawa.lovejvm.memory.heap;

/**
 * Heap is a thin wrapper of real memory space.
 * RawObject is a object that contains a reference to bytearray[address:address+size-1].
 * We retrieve fields data via rawObject from the heap
 */
interface Heap {
  /** store the bytes in heap area. */
  void allocate(byte[] bytes);

  /**
   * set the bytes from the address consecutively
   *
   * @param startingAddress starting address of where to put the value in Heap
   */
  void save(int startingAddress, byte[] bytes);

  /** @return byte arrays specified by the args */
  byte[] retrieve(int startingAddress, int size);

  /** @return head head address of available space */
  int headAddress();
}
