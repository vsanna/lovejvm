package dev.ishikawa.lovejvm.nativemethod.implementation;


import dev.ishikawa.lovejvm.memory.stringpool.StringPoolUtil;
import dev.ishikawa.lovejvm.rawclass.RawClass;
import dev.ishikawa.lovejvm.rawclass.type.JvmType;
import dev.ishikawa.lovejvm.vm.Frame;
import dev.ishikawa.lovejvm.vm.RawSystem;
import dev.ishikawa.lovejvm.vm.Word;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ClassNative {
  private final RawSystem rawSystem;
  private final Map<String, RawClass> primitiveClasses;

  public ClassNative(RawSystem rawSystem) {
    this.rawSystem = rawSystem;
    this.primitiveClasses =
        JvmType.primaryTypes.stream()
            .reduce(
                new HashMap<>(),
                (map, entry) -> {
                  map.put(
                      Objects.requireNonNull(entry.getPrimitiveName()),
                      rawSystem.methodAreaManager().lookupOrLoadClass(entry.getBoxTypeBinaryName()));
                  return map;
                },
                (mapA, mapB) -> {
                  mapA.putAll(mapB);
                  return mapA;
                });
  }

  public List<Word> getPrimitiveClass(Frame currentFrame) {
    var stringObjectId = currentFrame.getOperandStack().pop().getValue();
    String className =
        rawSystem.stringPool()
            .getLabelBy(stringObjectId)
            .orElseGet(() -> StringPoolUtil.getLabelByObjectId(stringObjectId, rawSystem));

    return Optional.ofNullable(primitiveClasses.get(className))
        .map(it -> List.of(Word.of(it.getClassObjectId())))
        .orElseThrow(() -> new RuntimeException("invalid primitive class is searched"));
  }

  public List<Word> forName0(Frame currentFrame) {
    // TODO: impl
    return List.of(Word.of(-100));
  }
}
