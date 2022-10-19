package dev.ishikawa.sample;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class PrintStreamTest {
  public static void main(String[] args) throws FileNotFoundException {
    PrintStream ps = new PrintStream("dummy");

    ps.println("hello");

    ps.println(0);
    ps.println(2147483647);
    ps.println(-2147483648);
    ps.println(123);

    ps.println(0L);
    ps.println(9223372036854775807L);
    ps.println(-9223372036854775808L);
    ps.println(123123123123123L);

//    ps.println(123.123D);
//    ps.println(123.123F);
//    ps.println(true);
//    ps.println(false);
//    ps.println((byte)123);
//    ps.println((short)123);
//    ps.println('c'); // this should fail
  }
}
