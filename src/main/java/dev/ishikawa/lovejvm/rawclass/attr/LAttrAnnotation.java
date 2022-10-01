package dev.ishikawa.lovejvm.rawclass.attr;


import java.util.List;

public class LAttrAnnotation {
  private final short typeIndex;
  private final short numElementValuePairs;
  private final List<LAttrAnnotationElementValuePair> elementValuePairs;

  public LAttrAnnotation(
      short typeIndex,
      short numElementValuePairs,
      List<LAttrAnnotationElementValuePair> elementValuePairs) {
    this.typeIndex = typeIndex;
    this.numElementValuePairs = numElementValuePairs;
    this.elementValuePairs = elementValuePairs;
  }

  public static class LAttrAnnotationElementValuePair {
    private final short elementNameIndex; // index of constantpool
    private final ElementValue value;

    public LAttrAnnotationElementValuePair(short elementNameIndex, ElementValue value) {
      this.elementNameIndex = elementNameIndex;
      this.value = value;
    }

    public static class ElementValue<T> {
      private final T value;

      public ElementValue(T value) {
        this.value = value;
      }
    }

    public static class ConstValueIndexElementValue extends ElementValue<Short> {
      public ConstValueIndexElementValue(Short value) {
        super(value);
      }
    }

    public static class EnumConstValueElementValue extends ElementValue<EnumConstValue> {
      public EnumConstValueElementValue(EnumConstValue value) {
        super(value);
      }
    }

    public static class EnumConstValue {
      private final short typeNameIndex;
      private final short constNameIndex;

      public EnumConstValue(short typeNameIndex, short constNameIndex) {
        this.typeNameIndex = typeNameIndex;
        this.constNameIndex = constNameIndex;
      }
    }

    public static class ClassInfoIndexElementValue extends ElementValue<Short> {
      public ClassInfoIndexElementValue(Short value) {
        super(value);
      }
    }

    public static class AnnotationElementValue extends ElementValue<LAttrAnnotation> {
      public AnnotationElementValue(LAttrAnnotation annotation) {
        super(annotation);
      }
    }

    public static class ArrayValueElementValue extends ElementValue<ArrayValue> {
      public ArrayValueElementValue(ArrayValue value) {
        super(value);
      }
    }

    public static class ArrayValue {
      private final short numValues;
      private final List<ElementValue> values;

      public ArrayValue(short numValues, List<ElementValue> values) {
        this.numValues = numValues;
        this.values = values;
      }
    }
  }
}
