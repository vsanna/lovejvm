package java.io;

import java.lang.String;

public class PrintStream {
    public PrintStream(String var1) throws FileNotFoundException {}
    public PrintStream(OutputStream var1) {}

    public void println() {
        write("\n");
    }

    public void println(boolean var1) {
        write(String.valueOf(var1));
    }

    public void println(char var1) {
        write(String.valueOf(var1));
    }

    public void println(int var1) {
       write(String.valueOf(var1));
    }

    public void println(long var1) {
        write(String.valueOf(var1));
    }

    public void println(float var1) {
        write(String.valueOf(var1));
    }

    public void println(double var1) {
        write(String.valueOf(var1));
    }

    public void println(char[] var1) {
        write(String.valueOf(var1));
    }

    public void println(String x) {
        write(x);
    }

    public void println(Object var1) {
        write(var1.toString());
    }

    public native void write(String message);
}
