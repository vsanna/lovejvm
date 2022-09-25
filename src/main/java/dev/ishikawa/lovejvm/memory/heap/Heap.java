package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

/**
 * heap - bytearray ... 隙間なく並べられたbytes - どこからどのclass(=どの長さ)のobjectがあるかの管理 -
 * bytearray[address:address+size-1]への参照を保持し、fieldを取り出せるもの=RawObject - addressをもとにrawObjectを取り出して返す
 * -
 */
interface Heap {
  /**
   * store the bytes in heap area.
   */
  void allocate(byte[] bytes);

  /**
   * set the word values from the address consecutively
   * @param address starting address of where to put the value in Heap
   */
  void setValue(int address, List<Word> value);

  /**
   * @return head head address of available space
   * */
  int headAddress();
}
