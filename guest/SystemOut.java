package dev.ishikawa.test;

import java.util.Properties;
import java.io.PrintStream;
import java.io.FileNotFoundException;

class SystemOut {
    static public void main() {
        try {
            // 今の問題 = System.getPropertyがnull返すこと
            System.setProperties(new Properties());
            System.setOut(new PrintStream("dummy"));
            System.out.println("hello");
        } catch (FileNotFoundException ex) {

        }
    }
}

