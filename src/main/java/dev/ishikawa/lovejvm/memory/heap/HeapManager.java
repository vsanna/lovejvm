package dev.ishikawa.lovejvm.memory.heap;


import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.rawclass.type.RawArrayClass;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public interface HeapManager {
  /**
   * allocate mem space according to the given class's object size.
   *
   * @return objectId
   * @param baseClass
   */
  int newObject(RawClass baseClass);

  /** set the value in heap */
  void setValue(RawObject rawObject, RawField rawField, List<Word> value);

  void setValue(RawObject rawObject, String fieldName, List<Word> value);

  List<Word> getValue(RawObject rawObject, RawField rawField);

  List<Word> getValue(RawObject rawObject, String fieldName);

  int newArrayObject(RawArrayClass rawArrayClass, int arrSize);

  void setElement(RawObject rawObject, int position, List<Word> value);

  List<Word> getElement(RawObject rawObject, int position);

  /** retrieve RawObject stored in the heap by the given objectId */
  RawObject lookupObject(int objectId);

  RawObject createClassObject(RawClass targetClass);

  void dump();
}
