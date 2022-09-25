package dev.ishikawa.lovejvm.memory.stringpool;

// TODO
// hello が utf8 entryとしてある
// string(name=1) entryが別にある
// string(name=1) のentryNumを参照する際に、constantPool.findBy(entryNum) = string entryが帰ってくる
// このstring entryを参照したタイミングでString objectをheapに作る
//   // RawSystem.stringMap.allocate()
// 以後、constantPool.findBy(entryNum)はString objectのref(=objectId)が帰ってくるようになる
// ... ともにintを返す前提で初めて成り立つ. 今ConstantPoolEntryを返すようにしてしまっている.
// 代替案として、ConstantStringにString objectへの参照 = objectIdをもたせる
// OR stringMap<String, RawObject(objectId)>をもつ ... こっちだな(上の方が理想的ではある)
// -> ConstantString entryを使うときに stringMap.getorCreate(label)
// // イメージとしては、ConstantString -> ConstantUtf8(//ここまではMethodAreaのCP) -> Heapからobject
public interface StringPool {
  /**
   * allocate the specified size mem, and give the objectId.
   *
   * @return objectId
   * @param label string literal
   */
  int register(String label);

  /** get objectId(=ObjectRef) of the given String literal */
  int getOrCreate(String label);
}
