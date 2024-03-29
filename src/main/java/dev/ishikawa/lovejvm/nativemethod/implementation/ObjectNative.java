package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;

public class ObjectNative {
  private final RawSystem rawSystem;

  public ObjectNative(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
  }

  public List<Word> hashCode(Frame currentFrame) {
    // Object#hashCode returns its objectId
    int objectId = currentFrame.getOperandStack().pop().getValue();
    return List.of(Word.of(objectId));
  }

  public List<Word> getClass(Frame currentFrame) {
    var v1 = currentFrame.getOperandStack().pop().getValue();
    var classObjectReference =
        rawSystem.heapManager().lookupObject(v1).getRawClass().getClassObjectId();
    return List.of(Word.of(classObjectReference));
  }
}
