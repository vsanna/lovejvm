package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.memory.stringpool.StringPoolUtil;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class StringNative {
  private final RawSystem rawSystem;

  public StringNative(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  public List<Word> intern(Frame currentFrame) {
    int stringObjectId = currentFrame.getOperandStack().pop().getValue();
    return rawSystem.stringPool()
        .getLabelBy(stringObjectId)
        .map((_label) -> List.of(Word.of(stringObjectId)))
        .orElseGet(
            () -> {
              RawObject stringRawObject = rawSystem.heapManager().lookupObject(stringObjectId);
              String label = StringPoolUtil.getLabelByObjectId(stringObjectId, rawSystem);
              return List.of(Word.of(rawSystem.stringPool().register(label, stringRawObject)));
            });
  }
}
