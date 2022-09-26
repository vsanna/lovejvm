package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.attr.AttrRuntimeVisibleAnnotations;
import dev.ishikawa.lovejvm.rawclass.attr.AttrRuntimeVisibleAnnotations.LAttrAnnotation;
import dev.ishikawa.lovejvm.rawclass.attr.AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair.ElementValue;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class AnnotationElementValueParser {
  public static Pair<Integer, ElementValue> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    // ElementValue

    AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair.ElementValue<?>
        value;

    String tag = String.valueOf((char) bytecode[pointer]);
    pointer += 1;

    switch (tag) {
      case "B":
      case "C":
      case "D":
      case "F":
      case "I":
      case "J":
      case "S":
      case "Z":
      case "s":
        // const
        value =
            new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                .ElementValue<>(
                new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                    .ConstValueIndexElementValue(
                    ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1])));
        pointer += 2;
        break;
      case "e":
        // enum
        value =
            new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                .ElementValue<>(
                new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                    .EnumConstValueElementValue(
                    new AttrRuntimeVisibleAnnotations.LAttrAnnotation
                        .LAttrAnnotationElementValuePair.EnumConstValue(
                        ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]),
                        ByteUtil.concat(bytecode[pointer + 2], bytecode[pointer + 3]))));
        pointer += 4;
        break;
      case "c":
        // class
        value =
            new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                .ElementValue<>(
                new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                    .ClassInfoIndexElementValue(
                    ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1])));
        pointer += 2;
        break;
      case "@":
        // annotation
        Pair<Integer, LAttrAnnotation> result =
            AnnotationParser.parse(pointer, bytecode, constantPool);
        LAttrAnnotation innerAnnotation = result.getRight();
        value =
            new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                .ElementValue<>(
                new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                    .AnnotationElementValue(innerAnnotation));
        pointer = result.getLeft();
        break;
      case "[":
        // array
        short numValues = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
        pointer += 2;

        List<ElementValue> values = new ArrayList<>(numValues);

        for (int i = 0; i < numValues; i++) {
          var parseResult = AnnotationElementValueParser.parse(pointer, bytecode, constantPool);
          values.add(parseResult.getRight());
          pointer = parseResult.getLeft();
        }

        var arrayValue =
            new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                .ArrayValue(numValues, values);
        value =
            new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                .ElementValue<>(
                new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair
                    .ArrayValueElementValue(arrayValue));
        break;
      default:
        throw new RuntimeException(
            String.format("unexpected annotation element value tag value: %s", tag));
    }

    return Pair.of(pointer, value);
  }
}
