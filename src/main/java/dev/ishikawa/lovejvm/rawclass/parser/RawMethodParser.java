package dev.ishikawa.lovejvm.rawclass.parser;

import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;

// TODO: merge with LFieldParser
public class RawMethodParser {
    public static Pair<Integer, RawMethod> parse(int pointer, byte[] bytecode, ConstantPool constantPool) {
        var parseAccessFlagResult = parseAccessFlag(pointer, bytecode);
        pointer = parseAccessFlagResult.getLeft();
        var accessFlag = parseAccessFlagResult.getRight();

        var parseNameAndDescResult = parseNameAndDesc(pointer, bytecode, constantPool);
        pointer = parseNameAndDescResult.getLeft();
        var name = parseNameAndDescResult.getRight().getLeft();
        var desc = parseNameAndDescResult.getRight().getRight();

        var parseAttrsResult = AttrsParser.parse(pointer, bytecode, constantPool);
        pointer = parseAttrsResult.getLeft();
        var attrs = parseAttrsResult.getRight();

        return Pair.of(pointer, new RawMethod(accessFlag, name, desc, attrs));
    }


    /**
     * @return Pair<Integer, Short> ... pair{pointer, accessField}
     * */
    private static Pair<Integer, Short> parseAccessFlag(int pointer, byte[] bytecode) {
        var accessFlag = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        return Pair.of(pointer, accessFlag);
    }

    private static Pair<Integer, Pair<ConstantUtf8, ConstantUtf8>> parseNameAndDesc(int pointer, byte[] bytecode, ConstantPool constantPool) {
        var nameIndex = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        var descIndex = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        return Pair.of(pointer, Pair.of(constantPool.findByIndex(nameIndex), constantPool.findByIndex(descIndex)));
    }
}
