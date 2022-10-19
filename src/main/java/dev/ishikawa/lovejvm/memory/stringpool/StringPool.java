package dev.ishikawa.lovejvm.memory.stringpool;


import dev.ishikawa.lovejvm.rawobject.RawObject;
import java.util.Optional;

/**
 * StringPool contains String object defined in classfiles.
 * It doesn't manage other String objects made by using String constructor. ex: new String(new byte[]{...})
 *
 * ※ String#intern returns a String object that hash the same label if StringPool has it.
 *   Otherwise it registers the receiver string object in it, and returns the same object.
 *   SEE  dev.ishikawa.lovejvm.nativemethod.implementation.StringNative
 * */
public interface StringPool {
  /**
   * get objectId(=ObjectRef) of the given String literal
   * */
  int getOrCreate(String label);

  Optional<String> getLabelBy(int objectId);

  /**
   * register an existing rawObject with a string label
   * */
  int register(String label, RawObject stringRawObject);
}
