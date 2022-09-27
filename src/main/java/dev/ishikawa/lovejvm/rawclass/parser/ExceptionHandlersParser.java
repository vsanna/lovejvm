package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.method.exceptionhandler.ExceptionHandlers;
import dev.ishikawa.lovejvm.rawclass.method.exceptionhandler.RawExceptionHandler;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class ExceptionHandlersParser {
  public static Pair<Integer, ExceptionHandlers> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    var entrySize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    List<RawExceptionHandler> entries = new ArrayList<>(entrySize);

    for (int i = 0; i < entrySize; i++) {
      int startPc = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;
      int endPc = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;
      int handlerPc = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;
      int catchTypeIndex = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;
      // when catchTypeIndex is zero, it means this exceptionHandler catches all exceptions.
      ConstantClass catchType = null;
      if (catchTypeIndex > 0) {
        catchType = (ConstantClass) constantPool.findByIndex(catchTypeIndex);
      }
      entries.add(new RawExceptionHandler(startPc, endPc, handlerPc, catchType));
    }

    return Pair.of(pointer, new ExceptionHandlers(entrySize, entries));
  }
}
