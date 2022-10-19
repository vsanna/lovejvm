package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.memory.stringpool.StringPoolUtil;
import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClassNative {
  private static final Map<String, RawClass> primitiveClasses =
      Map.ofEntries(
          Map.entry("int", RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Integer")),
          Map.entry("long", RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Long")),
          Map.entry("float", RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Float")),
          Map.entry("double", RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Double")),
          Map.entry("byte", RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Byte")),
          Map.entry("short", RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Short")),
          Map.entry("boolean", RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Boolean")),
          Map.entry("char", RawSystem.methodAreaManager.lookupOrLoadClass("java/lang/Character")));

  public static List<Word> getPrimitiveClass(Frame currentFrame) {
    var stringObjectId = currentFrame.getOperandStack().pop().getValue();
    String className =
        RawSystem.stringPool
            .getLabelBy(stringObjectId)
            .orElseGet(() -> StringPoolUtil.getLabelByObjectId(stringObjectId));

    return Optional.ofNullable(primitiveClasses.get(className))
        .map(it -> List.of(Word.of(it.getClassObjectId())))
        .orElseThrow(() -> new RuntimeException("invalid primitive class is searched"));
  }
}
