package dev.ishikawa.lovejvm.memory.methodarea;

/** MethodArea is a thin wrapper of mem space. */
interface MethodArea {
  /** save the given bytes in MethodArea's available area */
  void allocate(byte[] bytes);

  /** @return a byte located in the given address */
  byte lookupByte(int address);

  /** @return byte arrays specified by the args */
  byte[] retrieve(int startingAddress, int size);

  /** save the given bytes in the area specified by startingAddress */
  void save(int startingAddress, byte[] bytes);

  /** @return head head address of available space */
  int headAddress();

  /**
   * set 0 bytes in the specified area
   *
   * @param startingAddress starting address of MethodArea
   * @param staticAreaByteSize how many bytes to do zeroclear from the startingAddress
   */
  void clear(int startingAddress, int staticAreaByteSize);
}
