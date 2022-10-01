package dev.ishikawa.lovejvm.rawclass.attr;


import dev.ishikawa.lovejvm.rawclass.constantpool.entity.ConstantUtf8;

/** binary format: u2 attribute_name_index u4 attribute_length {attribute_length} data */
public abstract class Attr<T> {
  private final ConstantUtf8 attrName;
  private final int dataLength;
  private final T attrBody;

  protected Attr(ConstantUtf8 attrName, int dataLength, T attrBody) {
    this.attrName = attrName;
    this.dataLength = dataLength;
    this.attrBody = attrBody;
  }

  public ConstantUtf8 getAttrName() {
    return attrName;
  }

  public T getAttrBody() {
    return attrBody;
  }

  /**
   * @return the number of BYTE.
   * */
  public int size() {
    return 2 // attrName
        + 4 // num to show dataLength
        + dataLength;
  }
}
