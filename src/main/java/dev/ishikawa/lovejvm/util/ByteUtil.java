package dev.ishikawa.lovejvm.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

/** ByteUtil contains a bunch of util methods to handle bytes */
public class ByteUtil {
  /**
   * concat returns concatinated 16 bits as a short value.
   *
   * @param a a byte located in a higher position
   * @param b a byte located in a lower position
   * @return concatinated 16 bits as a short value
   */
  public static short concat(byte a, byte b) {
    return (short) ((0b1111111100000000 & (a << 8)) | (0b0000000011111111 & b));
  }

  /**
   * concat returns concatinated 32 bits as a int value.
   *
   * @param a a byte located in the first position
   * @param b a byte located in the second position
   * @param c a byte located in the third position
   * @param d a byte located in the forth position
   * @return concatinated 32 bits as a short value
   */
  public static int concat(byte a, byte b, byte c, byte d) {
    return (0b11111111000000000000000000000000 & (a << 24))
        | (0b00000000111111110000000000000000 & (b << 16))
        | (0b00000000000000001111111100000000 & (c << 8))
        | (0b00000000000000000000000011111111 & d);
  }

  /**
   * concat returns concatinated 64 bits as a long value.
   *
   * @param a a byte located in the first position
   * @param b a byte located in the second position
   * @param c a byte located in the third position
   * @param d a byte located in the forth position
   * @param e a byte located in the fifth position
   * @param f a byte located in the sixth position
   * @param g a byte located in the seventh position
   * @param h a byte located in the eighth position
   * @return concatinated 64 bits as a short value
   */
  public static long concat(byte a, byte b, byte c, byte d, byte e, byte f, byte g, byte h) {
    return (0b1111111100000000000000000000000000000000000000000000000000000000L & ((long) a << 56))
        | (0b0000000011111111000000000000000000000000000000000000000000000000L & ((long) b << 48))
        | (0b0000000000000000111111110000000000000000000000000000000000000000L & ((long) c << 40))
        | (0b0000000000000000000000001111111100000000000000000000000000000000L & ((long) d << 32))
        | (0b0000000000000000000000000000000011111111000000000000000000000000L & ((long) e << 24))
        | (0b0000000000000000000000000000000000000000111111110000000000000000L & ((long) f << 16))
        | (0b0000000000000000000000000000000000000000000000001111111100000000L & ((long) g << 8))
        | (0b0000000000000000000000000000000000000000000000000000000011111111L & ((long) h));
  }

  public static byte[] split(int value) {
    return new byte[] {
      ((byte) (0b11111111 & (value >> 24))),
      ((byte) (0b11111111 & (value >> 16))),
      ((byte) (0b11111111 & (value >> 8))),
      ((byte) (0b11111111 & value))
    };
  }

  /**
   * readBytesFromFilePath read binary from a file located in the given filePath
   *
   * @param filePath a path to the file to read binary from
   * @return binary data of the file's content
   */
  public static byte[] readBytesFromFilePath(String filePath) {
    byte[] fileBytes;
    try {
      Path file = Path.of(filePath);
      var inputStream = new FileInputStream(file.toFile());
      fileBytes = inputStream.readAllBytes();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("invalid file");
    }

    return fileBytes;
  }
}
