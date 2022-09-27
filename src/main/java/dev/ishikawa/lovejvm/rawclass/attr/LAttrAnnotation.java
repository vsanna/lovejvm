package dev.ishikawa.lovejvm.rawclass.attr;


import java.util.List;

public class LAttrAnnotation {
  private short typeIndex;
  private short numElementValuePairs;
  private List<LAttrAnnotationElementValuePair> elementValuePairs;

  public LAttrAnnotation(
      short typeIndex,
      short numElementValuePairs,
      List<LAttrAnnotationElementValuePair> elementValuePairs) {
    this.typeIndex = typeIndex;
    this.numElementValuePairs = numElementValuePairs;
    this.elementValuePairs = elementValuePairs;
  }

  public static class LAttrAnnotationElementValuePair {
    private short elementNameIndex; // index of constantpool
    private ElementValue value;

    public LAttrAnnotationElementValuePair(short elementNameIndex, ElementValue value) {
      this.elementNameIndex = elementNameIndex;
      this.value = value;
    }

    public static class ElementValue<T> {
      private T value;

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
      private short typeNameIndex;
      private short constNameIndex;

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
      private short numValues;
      private List<ElementValue> values;

      public ArrayValue(short numValues, List<ElementValue> values) {
        this.numValues = numValues;
        this.values = values;
      }
    }
  }
}
