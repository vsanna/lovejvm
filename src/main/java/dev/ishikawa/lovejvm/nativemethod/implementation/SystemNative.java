package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class SystemNative {
  private final RawSystem rawSystem;

  public SystemNative(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  public List<Word> setOut0(Frame currentFrame) {
    return setPrintStream(currentFrame, "out");
  }

  public List<Word> setIn0(Frame currentFrame) {
    return setPrintStream(currentFrame, "in");
  }

  public List<Word> setErr0(Frame currentFrame) {
    return setPrintStream(currentFrame, "err");
  }

  private List<Word> setPrintStream(Frame currentFrame, String fieldName) {
    var printStreamObjectId = currentFrame.getOperandStack().pop().getValue();
    var systemRawClass = rawSystem.methodAreaManager().lookupOrLoadClass("java/lang/System");
    var outField =
        systemRawClass
            .findStaticFieldBy(fieldName)
            .orElseThrow(() -> new RuntimeException("out field is not found"));
    rawSystem.methodAreaManager().putStaticFieldValue(
        systemRawClass, outField, List.of(Word.of(printStreamObjectId)));
    return List.of();
  }
}
