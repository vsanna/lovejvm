package dev.ishikawa.test;

import java.io.*;

class SystemOut {
    static public void main() {
        try {
            FileOutputStream outStream = new FileOutputStream("./output.txt");
            PrintStream printStream = new PrintStream(outStream);
            System.setOut(printStream);
            System.out.println("hello");
        } catch(IOException ex) {
        }
    }
}

