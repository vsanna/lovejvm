package dev.ishikawa.lovejvm.klass.parser;

import dev.ishikawa.lovejvm.klass.ExceptionHandlers;
import dev.ishikawa.lovejvm.klass.LAttr;
import dev.ishikawa.lovejvm.klass.LExceptionHandler;
import dev.ishikawa.lovejvm.klass.constantpool.Attrs;
import dev.ishikawa.lovejvm.klass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.klass.constantpool.LAttrParser;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ExceptionHandlersParser {
    public static Pair<Integer, ExceptionHandlers> parse(int pointer, byte[] bytecode, ConstantPool constantPool) {
        int entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer+1]);
        pointer += 2;
        List<LExceptionHandler> entries = new ArrayList<>(entrySize);

        for (int i = 0; i < entrySize; i++) {
            // TODO
        }

        return Pair.of(pointer, new ExceptionHandlers(entrySize, entries));
    }
}
