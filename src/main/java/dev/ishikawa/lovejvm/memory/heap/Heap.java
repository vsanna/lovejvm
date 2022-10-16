package dev.ishikawa.lovejvm.memory.heap;

/**
 * Heap is a thin wrapper of actual memory space.
 */
interface Heap {
  /**
   * store the bytes in heap area.
   *
   * @param bytes the data to store in heap area
   * */
  void allocate(byte[] bytes);

  /**
   * set the bytes from the given address consecutively
   *
   * @param startingAddress starting address of where to put the value in Heap
   * @param bytes the data to store in heap area
   */
  void save(int startingAddress, byte[] bytes);

  /**
   * retrieve byte array from Heap
   *
   * @param startingAddress starting address of where to retrieve the value in Heap
   * @param size how many bytes to retrieve
   * @return byte arrays specified by the args
   * */
  byte[] retrieve(int startingAddress, int size);

  /**
   * @return the head address of available space in Heap
   * */
  int headAddress();
}
