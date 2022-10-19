package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.memory.stringpool.StringPoolUtil;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class PrintStreamNative {
  public static List<Word> write(Frame currentFrame) {
    var stringObjectId = currentFrame.getOperandStack().pop().getValue();
    String label =
        RawSystem.stringPool
            .getLabelBy(stringObjectId)
            .orElseGet(() -> StringPoolUtil.getLabelByObjectId(stringObjectId));
    System.out.println(label);
    return List.of();
  }
}
