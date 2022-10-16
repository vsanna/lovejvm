package dev.ishikawa.lovejvm.memory.stringpool;

public interface StringPool {
  /**
   * get objectId(=ObjectRef) of the given String literal
   * */
  int getOrCreate(String label);
}
