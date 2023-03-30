package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class FloatNative {
  public List<Word> floatToRawIntBits(Frame currentFrame) {
    var v1 = currentFrame.getOperandStack().pop().getValue();
    int result = Float.floatToRawIntBits(ByteUtil.convertToFloat(v1));
    return List.of(Word.of(result));
  }
}
