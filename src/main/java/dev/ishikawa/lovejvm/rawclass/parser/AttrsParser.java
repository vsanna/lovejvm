package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.attr.Attr;
import dev.ishikawa.lovejvm.rawclass.attr.Attrs;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class AttrsParser {
  public static Pair<Integer, Attrs> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    List<Attr> entries = new ArrayList<>(entrySize);

    for (int i = 0; i < entrySize; i++) {
      var result = AttrParser.parseAttrData(pointer, bytecode, constantPool);
      pointer = result.getLeft();
      entries.add(result.getRight());
    }

    return Pair.of(pointer, new Attrs(entrySize, entries));
  }
}
