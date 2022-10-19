package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.memory.stringpool.StringPoolUtil;
import dev.ishikawa.lovejvm.rawobject.RawObject;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class StringNative {
  public static List<Word> intern(Frame currentFrame) {
    int stringObjectId = currentFrame.getOperandStack().pop().getValue();
    return RawSystem.stringPool
        .getLabelBy(stringObjectId)
        .map((_label) -> List.of(Word.of(stringObjectId)))
        .orElseGet(
            () -> {
              RawObject stringRawObject = RawSystem.heapManager.lookupObject(stringObjectId);
              String label = StringPoolUtil.getLabelByObjectId(stringObjectId);
              return List.of(Word.of(RawSystem.stringPool.register(label, stringRawObject)));
            });
  }
}
