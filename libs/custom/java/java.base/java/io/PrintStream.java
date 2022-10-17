package java.io;

import java.lang.String;

public class PrintStream {
    public PrintStream(String var1) throws FileNotFoundException {}
    public PrintStream(OutputStream var1) {}

    public void println() {
    }

    public void println(boolean var1) {
    }

    public void println(char var1) {
    }

    public native void println(int var1);

    public void println(long var1) {
    }

    public void println(float var1) {
    }

    public void println(double var1) {
    }

    public void println(char[] var1) {
    }

    // LoveJVM provides a dummy implementation
    public native void println(String x);

    public void println(Object var1) {}
}
