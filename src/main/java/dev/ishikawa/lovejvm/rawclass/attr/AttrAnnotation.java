package dev.ishikawa.lovejvm.rawclass.attr;


import java.util.List;

public class AttrAnnotation {
  private final short typeIndex;
  private final short numElementValuePairs;
  private final List<AttrAnnotationElementValuePair> elementValuePairs;

  public AttrAnnotation(
      short typeIndex,
      short numElementValuePairs,
      List<AttrAnnotationElementValuePair> elementValuePairs) {
    this.typeIndex = typeIndex;
    this.numElementValuePairs = numElementValuePairs;
    this.elementValuePairs = elementValuePairs;
  }

  public static class AttrAnnotationElementValuePair {
    private final short elementNameIndex; // index of constantpool
    private final ElementValue value;

    public AttrAnnotationElementValuePair(short elementNameIndex, ElementValue value) {
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

    public static class AnnotationElementValue extends ElementValue<AttrAnnotation> {
      public AnnotationElementValue(AttrAnnotation annotation) {
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
