package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class IntegerNative {
  public static List<Word> toString(Frame currentFrame) {
    var intValue = currentFrame.getOperandStack().pop().getValue();
    return List.of(Word.of(RawSystem.stringPool.getOrCreate(String.valueOf(intValue))));
  }
}
