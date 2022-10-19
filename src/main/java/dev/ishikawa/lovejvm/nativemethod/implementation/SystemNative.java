package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class SystemNative {
  public static List<Word> setOut0(Frame currentFrame) {
    return setPrintStream(currentFrame, "out");
  }

  public static List<Word> setIn0(Frame currentFrame) {
    return setPrintStream(currentFrame, "in");
  }

  public static List<Word> setErr0(Frame currentFrame) {
    return setPrintStream(currentFrame, "err");
  }

  private static List<Word> setPrintStream(Frame currentFrame, String fieldName) {
    var printStreamObjectId = currentFrame.getOperandStack().pop().getValue();
    var systemRawClass = RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/System");
    var outField =
        systemRawClass
            .findStaticFieldBy(fieldName)
            .orElseThrow(() -> new RuntimeException("out field is not found"));
    RawSystem.methodAreaManager.putStaticFieldValue(
        systemRawClass, outField, List.of(Word.of(printStreamObjectId)));
    return List.of();
  }
}
