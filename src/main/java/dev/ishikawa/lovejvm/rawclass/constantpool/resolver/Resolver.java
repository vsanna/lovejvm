package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

public interface Resolver<T> {
  /**
   * タイプ別にresolve 1. 必要ならobjectを作る 2. そのobjectへの参照をConstantPoolEntryにset 3. entryのisResolveをtrueにset
   */
  void resolve(ConstantPool constantPool, T entry);
}
