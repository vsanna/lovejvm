package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/**
 * heap - bytearray ... 隙間なく並べられたbytes - どこからどのclass(=どの長さ)のobjectがあるかの管理 -
 * bytearray[address:address+size-1]への参照を保持し、fieldを取り出せるもの=RawObject - addressをもとにrawObjectを取り出して返す
 * -
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

  /**
   * @return byte arrays specified by the args
   * */
  byte[] retrieve(int startingAddress, int size);

  /** @return head head address of available space */
  int headAddress();
}
