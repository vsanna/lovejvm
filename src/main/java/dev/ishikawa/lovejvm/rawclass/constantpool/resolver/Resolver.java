package dev.ishikawa.lovejvm.rawclass.constantpool.resolver;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;

/**
 * Resolution is the process of dynamically determining one or more concrete values from a symbolic
 * reference in the run-time constant pool.
 *
 * <p>Initially, all symbolic references in the run-time constant pool are unresolved.
 *
 * <p>constant pool entry 1. symbolic reference = symbolic references, which may later be resolved
 * 2. static
 *
 * <p>TODO: 結局、resolveした結果が何かがわからない 特にloadableかつresolvableなものの場合、resolveした結果 = reference to
 * Xをstackに乗せるはず... ref:
 * https://stackoverflow.com/questions/61654407/how-references-are-stored-in-java ここでは参照 = address.
 * とにかく指し示す先のものが辿れればよいようだ。
 *
 * <p>reference to a class => 別にclass objectでなくともよいっぽい。rawClassにIDふってそれを使うでも可能 reference to a field
 * => 別にfield objectでなくともよいっぽい。 reference to a method => 別にmethod objectでなくとも良いっぽい。 reference to a
 * methodtype => reference to an instance of java.lang.invoke.methodType reference to a methodhandle
 * => reference to an instance of java.lang.invoke.methodhandle reference to a dynamically computed
 * constant => ??? reference to a dynamically computed call site => ???
 *
 * <p>- group1: symbolic 1. a class or interface, 2. a field, 3. a method, 4. a method type, ...
 * reference 5. a method handle, 6. a dynamically-computed constant - group2: symbolic 7. a
 * dynamically-computed call site - group3: static 8. a string - > The string constant is a
 * reference to the new instance - string constantそのものがreference? 別にobjectIdを持たなくてもよいのか? -
 * そも、stringはstaticだからresolveされるもの 9. long, float, long, double - group4: static 10. name_and_type,
 * module, package, utf8
 */
public interface Resolver<T> {
  /**
   * タイプ別にresolve 1. 必要ならobjectを作る 2. そのobjectへの参照をConstantPoolEntryにset 3. entryのisResolveをtrueにset
   */
  void resolve(ConstantPool constantPool, T entry);
}
