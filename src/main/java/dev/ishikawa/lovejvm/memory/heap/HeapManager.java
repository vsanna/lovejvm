package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public interface HeapManager {
  /**
   * allocate the specified size mem, and give the objectId. If the baseClass doesn't exist yet,
   * ClassLoader will load it.
   *
   * @return objectId
   * @param baseClass
   */
  int register(RawClass baseClass);

  /** set the value in heap */
  void setValue(RawObject rawObject, RawField rawField, List<Word> value);

  List<Word> getValue(RawObject rawObject, RawField rawField);

  int registerArray(RawArrayClass rawArrayClass, int arrSize);

  void setElement(RawObject rawObject, int position, List<Word> value);

  List<Word> getElement(RawObject rawObject, int position);

  /** retrieve RawObject stored in the heap by the given objectId */
  RawObject lookupObject(int objectId);

  RawObject createClassObject(RawClass targetClass);

  void dump();
}
