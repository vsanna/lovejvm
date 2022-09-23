package dev.ishikawa.lovejvm.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class ByteUtil {
  public static short concat(byte a, byte b) {
    return (short) ((0b1111111100000000 & (a << 8)) | (0b0000000011111111 & b));
  }

  public static int concat(byte a, byte b, byte c, byte d) {
    return (0b11111111000000000000000000000000 & (a << 8 << 8 << 8))
        | (0b00000000111111110000000000000000 & (b << 8 << 8))
        | (0b00000000000000001111111100000000 & (c << 8))
        | (0b00000000000000000000000011111111 & d);
  }

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
