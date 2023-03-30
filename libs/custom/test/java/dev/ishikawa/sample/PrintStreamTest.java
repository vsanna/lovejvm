package dev.ishikawa.sample;

import java.io.PrintStream;
import java.io.FileNotFoundException;

class PrintStreamTest {
  public static void main(String[] args) throws FileNotFoundException {
    PrintStream ps = new PrintStream("dummy");

    System.out.println("hello");

    System.out.println(Integer.MAX_VALUE);
    System.out.println(Integer.MIN_VALUE);
    System.out.println(123);

    System.out.println(Long.MAX_VALUE);
    System.out.println(Long.MIN_VALUE);
    System.out.println(123L);

    System.out.println(Float.MAX_VALUE);
    System.out.println(Float.MIN_VALUE);
    System.out.println(1.23f);

    System.out.println(Double.MAX_VALUE);
    System.out.println(Double.MIN_VALUE);
    System.out.println(3.21d);

    System.out.println(Byte.MAX_VALUE);
    System.out.println(Byte.MIN_VALUE);
    System.out.println(((byte) 10));

    System.out.println(Short.MAX_VALUE);
    System.out.println(Short.MIN_VALUE);
    System.out.println(((short) 100));

    System.out.println(Character.MAX_VALUE);
    System.out.println(Character.MIN_VALUE);
    System.out.println('a');
  }
}