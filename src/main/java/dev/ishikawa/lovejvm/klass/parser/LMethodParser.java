package dev.ishikawa.lovejvm.klass.parser;

import dev.ishikawa.lovejvm.klass.LAttr;
import dev.ishikawa.lovejvm.klass.LField;
import dev.ishikawa.lovejvm.klass.LMethod;
import dev.ishikawa.lovejvm.klass.constantpool.Attrs;
import dev.ishikawa.lovejvm.klass.constantpool.AttrsParser;
import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.klass.constantpool.LAttrParser;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;

import java.util.ArrayList;
import java.util.List;

// TODO: merge with LFieldParser
public class LMethodParser {
    public static Pair<Integer, LMethod> parse(int pointer, byte[] bytecode, ConstantPool constantPool) {
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

        return Pair.of(pointer, new LMethod(accessFlag, name, desc, attrs));
    }


    /**
     * @return Pair<Integer, Integer> ... pair{pointer, accessField}
     * */
    private static Pair<Integer, Integer> parseAccessFlag(int pointer, byte[] bytecode) {
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
