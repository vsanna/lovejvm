package java.lang;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public final class System {
    public static final PrintStream out = null;
    public static final PrintStream err = null;

    private System() {}

    static {
        try {
            setOut(new PrintStream("dummy"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setOut(PrintStream out) {
        setOut0(out);
    }

    // VM forcefully set the out
    private static native void setOut0(PrintStream out);

    public static String getProperty(String key) {
        throw new UnsupportedOperationException("");
    }
}
