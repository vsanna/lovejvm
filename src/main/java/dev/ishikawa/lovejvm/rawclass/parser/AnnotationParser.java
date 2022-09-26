package dev.ishikawa.lovejvm.rawclass.parser;


import dev.ishikawa.lovejvm.rawclass.attr.AttrRuntimeVisibleAnnotations;
import dev.ishikawa.lovejvm.rawclass.attr.AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair.ElementValue;
import dev.ishikawa.lovejvm.rawclass.constantpool.ConstantPool;
import dev.ishikawa.lovejvm.util.ByteUtil;
import dev.ishikawa.lovejvm.util.Pair;
import java.util.ArrayList;

public class AnnotationParser {
  public static Pair<Integer, AttrRuntimeVisibleAnnotations.LAttrAnnotation> parse(
      int pointer, byte[] bytecode, ConstantPool constantPool) {
    // Annotation
    short typeIndex = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;
    short numElementValuePairs = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
    pointer += 2;

    var elementValuePairs =
        new ArrayList<
            AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair>(
            numElementValuePairs);
    for (int j = 0; j < numElementValuePairs; j++) {
      // ElementValuePairs
      short elementNameIndex = ByteUtil.concat(bytecode[pointer], bytecode[pointer + 1]);
      pointer += 2;

      Pair<Integer, ElementValue> annotationElementValueParseResult =
          AnnotationElementValueParser.parse(pointer, bytecode, constantPool);

      var elementValue = annotationElementValueParseResult.getRight();
      pointer = annotationElementValueParseResult.getLeft();

      var pair =
          new AttrRuntimeVisibleAnnotations.LAttrAnnotation.LAttrAnnotationElementValuePair(
              elementNameIndex, elementValue);
      elementValuePairs.add(pair);
    }

    var annotation =
        new AttrRuntimeVisibleAnnotations.LAttrAnnotation(
            typeIndex, numElementValuePairs, elementValuePairs);
    return Pair.of(pointer, annotation);
  }
}
