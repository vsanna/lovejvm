package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.util.ByteUtil;

public class Word {
  // 4bytes.
  private byte[] bytes;

  public Word(byte[] bytes) {
    assert bytes.length == 4;
    this.bytes = bytes;
  }

  public int getValue() {
    return ByteUtil.concat(bytes[0], bytes[1], bytes[2], bytes[3]);
  }

  static Word of(byte b) {
    return new Word(new byte[] {0x00, 0x00, 0x00, b});
  }

  static Word of(byte a, byte b) {
    return new Word(new byte[] {0x00, 0x00, a, b});
  }

  static Word of(byte a, byte b, byte c) {
    return new Word(new byte[] {0x00, a, b, c});
  }

  static Word of(byte a, byte b, byte c, byte d) {
    return new Word(new byte[] {a, b, c, d});
  }

  static Word of(int a) {
    return new Word(
        new byte[] {
          (byte) ((a >> 8 >> 8 >> 8) & 0b11111111),
          (byte) ((a >> 8 >> 8) & 0b11111111),
          (byte) ((a >> 8) & 0b11111111),
          (byte) (a & 0b11111111)
        });
  }

  @Override
  public String toString() {
    return String.format(
        "Word(%x,%x,%x,%x: %d)", bytes[0], bytes[1], bytes[2], bytes[3], this.getValue());
  }
}
