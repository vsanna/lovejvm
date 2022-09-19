package dev.ishikawa.lovejvm.klass.constantpool;

import dev.ishikawa.lovejvm.klass.LAttr;
import dev.ishikawa.lovejvm.klass.LAttrConstantValue;
import dev.ishikawa.lovejvm.klass.constantpool.entity.ConstantUtf8;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AttrsParser {
    public static Pair<Integer, Attrs> parse(int pointer, byte[] bytecode, ConstantPool constantPool) {
        var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        List<LAttr> entries = new ArrayList<>(entrySize);

        for (int i = 0; i < entrySize; i++) {
            // parse constant pool entry
            var result = LAttrParser.parse(pointer, bytecode, constantPool);
            pointer = result.getLeft();
            entries.add(result.getRight());
        }

        return Pair.of(pointer, new Attrs(entrySize, entries));
    }
}
