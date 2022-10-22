package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair.AnnotationElementValue;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair.ArrayValue;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair.ArrayValueElementValue;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair.ClassInfoIndexElementValue;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair.ElementValue;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair.EnumConstValue;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair.EnumConstValueElementValue;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class AnnotationElementValueParser {
  public static Pair<Integer, ElementValue> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    // ElementValue

    AttrAnnotationElementValuePair.ElementValue<?> value;

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
            new AttrAnnotationElementValuePair.ElementValue<>(
                new AttrAnnotationElementValuePair.ConstValueIndexElementValue(
                    ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1])));
        pointer += 2;
        break;
      case "e":
        // enum
        value =
            new ElementValue<>(
                new EnumConstValueElementValue(
                    new EnumConstValue(
                        ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]),
                        ByteUtil.concatToShort(bytecode[pointer + 2], bytecode[pointer + 3]))));
        pointer += 4;
        break;
      case "c":
        // class
        value =
            new ElementValue<>(
                new ClassInfoIndexElementValue(
                    ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1])));
        pointer += 2;
        break;
      case "@":
        // annotation
        Pair<Integer, AttrAnnotation> result =
            AnnotationParser.parse(pointer, bytecode, constantPool);
        AttrAnnotation innerAnnotation = result.getRight();
        value = new ElementValue<>(new AnnotationElementValue(innerAnnotation));
        pointer = result.getLeft();
        break;
      case "[":
        // array
        short numValues = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
        pointer += 2;

        List<ElementValue> values = new ArrayList<>(numValues);

        for (int i = 0; i < numValues; i++) {
          var parseResult = AnnotationElementValueParser.parse(pointer, bytecode, constantPool);
          values.add(parseResult.getRight());
          pointer = parseResult.getLeft();
        }

        var arrayValue = new ArrayValue(numValues, values);
        value = new ElementValue<>(new ArrayValueElementValue(arrayValue));
        break;
      default:
        throw new RuntimeException(
            String.format("unexpected annotation element value tag value: %s", tag));
    }

    return Pair.of(pointer, value);
  }
}
