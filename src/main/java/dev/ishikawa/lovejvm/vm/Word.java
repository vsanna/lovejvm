package dev.ishikawa.lovejvm.vm;


import dev.ishikawa.lovejvm.util.ByteUtil;
import java.util.ArrayList;
import java.util.List;

public class Word {
  // 32 bits per one word
  private int value;

  public Word(int value) {
    this.value = value;
  }

  private Word(byte[] bytes) {
    assert bytes.length == BYTES_SIZE;
    this.value = ByteUtil.concatToInt(bytes[0], bytes[1], bytes[2], bytes[3]);
  }

  public int getValue() {
    return value;
  }

  public byte[] getBytes() {
    return ByteUtil.splitToBytes(value);
  }

  public static Word of(Word word) {
    return Word.of(word.getValue());
  }

  public static Word of(byte b) {
    return new Word(new byte[] {0x00, 0x00, 0x00, b});
  }

  public static Word of(byte a, byte b) {
    return new Word(new byte[] {0x00, 0x00, a, b});
  }

  public static Word of(byte a, byte b, byte c) {
    return new Word(new byte[] {0x00, a, b, c});
  }

  public static Word of(byte a, byte b, byte c, byte d) {
    return new Word(new byte[] {a, b, c, d});
  }

  public static Word of(int a) {
    return new Word(a);
  }

  public static Word of(float a) {
    return new Word(Float.floatToIntBits(a));
  }

  public static Word of(boolean a) {
    return Word.of(a ? 1 : 0);
  }

  public static List<Word> of(long a) {
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

  public static List<Word> of(double a) {
    long longbits = Double.doubleToLongBits(a);
    return Word.of(longbits);
  }

  public static List<Word> of(byte[] bytes) {
    assert (bytes.length % 4) == 0;

    var words = new ArrayList<Word>();
    int idx = 0;

    while (bytes.length > idx) {
      words.add(Word.of(bytes[idx], bytes[idx + 1], bytes[idx + 2], bytes[idx + 3]));
      idx += Word.BYTES_SIZE;
    }

    return words;
  }

  public static byte[] toByteArray(List<Word> words) {
    byte[] bytes = new byte[words.size() * Word.BYTES_SIZE];
    for (int i = 0; i < words.size(); i++) {
      var bytearr = words.get(i).getBytes();
      System.arraycopy(bytearr, 0, bytes, i * 4, bytearr.length);
    }
    return bytes;
  }

  @Override
  public String toString() {
    var bytes = this.getBytes();
    return String.format(
        "Word(%x,%x,%x,%x: %d)", bytes[0], bytes[1], bytes[2], bytes[3], this.getValue());
  }

  public static final int BITS_SIZE = 32;
  public static final int BYTES_SIZE = 4;
}
