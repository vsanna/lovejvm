package dev.ishikawa.lovejvm.nativemethod;


import dev.ishikawa.lovejvm.rawclass.method.RawMethod;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public interface NativeMethodHandler {
  List<Word> handle(RawMethod rawMethod, Frame currentFrame);
}
