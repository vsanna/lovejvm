package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantClass;
import dev.ishikawa.lovejvm.rawclass.method.exceptionhandler.ExceptionInfo;
import dev.ishikawa.lovejvm.rawclass.method.exceptionhandler.ExceptionInfoTable;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class ExceptionHandlersParser {
  public static Pair<Integer, ExceptionInfoTable> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    var entrySize = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    List<ExceptionInfo> entries = new ArrayList<>(entrySize);

    for (int i = 0; i < entrySize; i++) {
      int startPc = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;
      int endPc = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;
      int handlerPc = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;
      int catchTypeIndex = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;
      // when catchTypeIndex is zero, it means this exceptionHandler catches all exceptions.
      ConstantClass catchType = null;
      if (catchTypeIndex > 0) {
        catchType = (ConstantClass) constantPool.findByIndex(catchTypeIndex);
      }
      entries.add(new ExceptionInfo(startPc, endPc, handlerPc, catchType));
    }

    return Pair.of(pointer, new ExceptionInfoTable(entrySize, entries));
  }
}
