package dev.ishikawa.lovejvm.memory.heap;

import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public interface HeapManager {
  /**
   * allocate the specified size mem, and give the objectId.
   *
   * @return objectId
   * @param baseClass
   */
  int register(RawClass baseClass);

  /** retrieve RawObject stored in the heap by the given objectId */
  RawObject lookupObject(int objectId);

  /**
   * set the word values from the address consecutively
   *
   * @param address starting address of where to put the value in Heap
   */
  void setValue(int address, List<Word> value);
}
