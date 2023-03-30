package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.memory.stringpool.StringPoolUtil;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class PrintStreamNative {
  private final RawSystem rawSystem;

  public PrintStreamNative(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  public List<Word> write(Frame currentFrame) {
    var stringObjectId = currentFrame.getOperandStack().pop().getValue();
    String label =
        rawSystem.stringPool()
            .getLabelBy(stringObjectId)
            .orElseGet(() -> StringPoolUtil.getLabelByObjectId(stringObjectId, rawSystem));

    System.out.println(label);
    return List.of();
  }
}
