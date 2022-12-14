package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.rawclass.field.RawField;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;

public class RawFieldParser {
  public static Pair<Integer, RawField> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    var parseAccessFlagResult = parseAccessFlag(pointer, bytecode);
    pointer = parseAccessFlagResult.getLeft();
    int accessFlag = parseAccessFlagResult.getRight();

    var parseNameAndDescResult = parseNameAndDesc(pointer, bytecode, constantPool);
    pointer = parseNameAndDescResult.getLeft();
    var name = parseNameAndDescResult.getRight().getLeft();
    var desc = parseNameAndDescResult.getRight().getRight();

    var parseAttrsResult = AttrsParser.parse(pointer, bytecode, constantPool);
    pointer = parseAttrsResult.getLeft();
    var attrs = parseAttrsResult.getRight();

    return Pair.of(pointer, new RawField(accessFlag, name, desc, attrs));
  }

  /** @return Pair<Integer, Short> ... pair{pointer, accessField} */
  private static Pair<Integer, Short> parseAccessFlag(int pointer, byte[] bytecode) {
    var accessFlag = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    return Pair.of(pointer, accessFlag);
  }

  private static Pair<Integer, Pair<ConstantUtf8, ConstantUtf8>> parseNameAndDesc(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    var nameIndex = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    var descIndex = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    return Pair.of(
        pointer,
        Pair.of(
            (ConstantUtf8) constantPool.findByIndex(nameIndex),
            (ConstantUtf8) constantPool.findByIndex(descIndex)));
  }
}
