package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair;
import dev.ishikawa.lovejvm.rawclass.attr.AttrAnnotation.AttrAnnotationElementValuePair.ElementValue;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class AnnotationParser {
  public static Pair<Integer, List<AttrAnnotation>> parseList(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    var annotationSize = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;

    var entries = new ArrayList<AttrAnnotation>(annotationSize);
    for (int i = 0; i < annotationSize; i++) {
      Pair<Integer, AttrAnnotation> result = parse(pointer, bytecode, constantPool);
      pointer = result.getLeft();
      entries.add(result.getRight());
    }

    return Pair.of(pointer, entries);
  }

  public static Pair<Integer, AttrAnnotation> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    // Annotation
    short typeIndex = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    short numElementValuePairs = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;

    var elementValuePairs = new ArrayList<AttrAnnotationElementValuePair>(numElementValuePairs);
    for (int j = 0; j < numElementValuePairs; j++) {
      // ElementValuePairs
      short elementNameIndex = ByteUtil.concatToShort(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;

      Pair<Integer, ElementValue> annotationElementValueParseResult =
          AnnotationElementValueParser.parse(pointer, bytecode, constantPool);

      var elementValue = annotationElementValueParseResult.getRight();
      pointer = annotationElementValueParseResult.getLeft();

      var pair = new AttrAnnotationElementValuePair(elementNameIndex, elementValue);
      elementValuePairs.add(pair);
    }

    var annotation = new AttrAnnotation(typeIndex, numElementValuePairs, elementValuePairs);
    return Pair.of(pointer, annotation);
  }
}
