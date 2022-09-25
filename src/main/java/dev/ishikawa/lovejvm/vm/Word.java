package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.util.ByteUtil;
import java.util.List;

public class Word {
  private byte[] bytes;

  public Word(byte[] bytes) {
    assert bytes.length == BYTES_SIZE;
    this.bytes = bytes;
  }

  public int getValue() {
    return ByteUtil.concat(bytes[0], bytes[1], bytes[2], bytes[3]);
  }

  public byte[] getBytes() {
    return bytes;
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

  static List<Word> of(long a) {
    return List.of(
        new Word(
            new byte[] {
              (byte) ((a >> 56) & 0b11111111),
              (byte) ((a >> 48) & 0b11111111),
              (byte) ((a >> 40) & 0b11111111),
              (byte) ((a >> 32) & 0b11111111)
            }),
        new Word(
            new byte[] {
              (byte) ((a >> 24) & 0b11111111),
              (byte) ((a >> 16) & 0b11111111),
              (byte) ((a >> 8) & 0b11111111),
              (byte) (a & 0b11111111)
            }));
  }

  // copy factory
  static Word of(Word word) {
    return Word.of(word.getValue());
  }

  @Override
  public String toString() {
    return String.format(
        "Word(%x,%x,%x,%x: %d)", bytes[0], bytes[1], bytes[2], bytes[3], this.getValue());
  }

  public static final int BITS_SIZE = 32;
  public static final int BYTES_SIZE = 4;
}
