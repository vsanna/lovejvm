package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.attr.LAttrAnnotation;
import dev.ishikawa.lovejvm.rawclass.attr.LAttrAnnotation.LAttrAnnotationElementValuePair.ElementValue;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;
import java.util.List;

public class AnnotationParser {
  public static Pair<Integer, List<LAttrAnnotation>> parseList(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    var annotationSize = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;

    var entries = new ArrayList<LAttrAnnotation>(annotationSize);
    for (int i = 0; i < annotationSize; i++) {
      Pair<Integer, LAttrAnnotation> result = parse(pointer, bytecode, constantPool);
      pointer = result.getLeft();
      entries.add(result.getRight());
    }

    return Pair.of(pointer, entries);
  }

  public static Pair<Integer, LAttrAnnotation> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    // Annotation
    short typeIndex = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    short numElementValuePairs = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;

    var elementValuePairs =
        new ArrayList<LAttrAnnotation.LAttrAnnotationElementValuePair>(numElementValuePairs);
    for (int j = 0; j < numElementValuePairs; j++) {
      // ElementValuePairs
      short elementNameIndex = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;

      Pair<Integer, ElementValue> annotationElementValueParseResult =
          AnnotationElementValueParser.parse(pointer, bytecode, constantPool);

      var elementValue = annotationElementValueParseResult.getRight();
      pointer = annotationElementValueParseResult.getLeft();

      var pair =
          new LAttrAnnotation.LAttrAnnotationElementValuePair(elementNameIndex, elementValue);
      elementValuePairs.add(pair);
    }

    var annotation = new LAttrAnnotation(typeIndex, numElementValuePairs, elementValuePairs);
    return Pair.of(pointer, annotation);
  }
}
