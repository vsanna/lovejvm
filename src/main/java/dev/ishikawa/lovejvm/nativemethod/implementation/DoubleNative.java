package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class DoubleNative {
  private final RawSystem rawSystem;

  public DoubleNative(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  public List<Word> toString(Frame currentFrame) {
    var doubleValue =
        ByteUtil.concatToDouble(
            currentFrame.getOperandStack().pop().getValue(),
            currentFrame.getOperandStack().pop().getValue());
    int stringObjectId = rawSystem.stringPool().getOrCreate(Double.toString(doubleValue));
    return List.of(Word.of(stringObjectId));
  }

  public List<Word> doubleToRawLongBits(Frame currentFrame) {
    var v1 = currentFrame.getOperandStack().pop().getValue();
    var v2 = currentFrame.getOperandStack().pop().getValue();
    long result = Double.doubleToRawLongBits(ByteUtil.concatToDouble(v1, v2));
    return Word.of(result);
  }

  public List<Word> longBitsToDouble(Frame currentFrame) {
    var v1 = currentFrame.getOperandStack().pop().getValue();
    var v2 = currentFrame.getOperandStack().pop().getValue();
    double result = Double.longBitsToDouble(ByteUtil.concatToLong(v1, v2));
    return Word.of(result);
  }
}
