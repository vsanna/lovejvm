package java.io;

import java.lang.String;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Formatter;
import java.util.Locale;
import java.util.Locale.Category;

public class PrintStream extends FilterOutputStream implements Appendable, Closeable {
    public PrintStream(OutputStream var1) {
        super(null);
    }

    public PrintStream(OutputStream var1, boolean var2) {
        super(null);
    }

    public PrintStream(OutputStream var1, boolean var2, String var3) throws UnsupportedEncodingException {
        super(null);
    }

    public PrintStream(OutputStream var1, boolean var2, Charset var3) {
        super(null);
    }

    public PrintStream(String var1) throws FileNotFoundException {
        super(null);
    }

    public PrintStream(String var1, String var2) throws FileNotFoundException, UnsupportedEncodingException {
        super(null);
    }

    public PrintStream(String var1, Charset var2) throws IOException {
        super(null);
    }

    public PrintStream(File var1) throws FileNotFoundException {
        super(null);
    }

    public PrintStream(File var1, String var2) throws FileNotFoundException, UnsupportedEncodingException {
        super(null);
    }

    public PrintStream(File var1, Charset var2) throws IOException {
        super(null);
    }

    public void flush() {
    }

    public void close() {
    }

    public boolean checkError() {
        return false;
    }

    protected void setError() {
    }

    protected void clearError() {
    }

    public void write(int var1) {
    }

    public void write(byte[] var1, int var2, int var3) {
    }

    public void write(byte[] var1) throws IOException {
    }

    public void writeBytes(byte[] var1) {
    }
    public void print(boolean var1) {
    }

    public void print(char var1) {
    }

    public void print(int var1) {
    }

    public void print(long var1) {
    }

    public void print(float var1) {
    }

    public void print(double var1) {
    }

    public void print(char[] var1) {
    }

    public void print(String var1) {
    }

    public void print(Object var1) {
    }

    public void println() {
    }

    public void println(boolean var1) {
    }

    public void println(char var1) {
    }

    public void println(int var1) {
    }

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

    public void println(Object var1) {
    }

    public PrintStream printf(String var1, Object... var2) {
        return this;
    }

    public PrintStream printf(Locale var1, String var2, Object... var3) {
        return this;
    }

    public PrintStream format(String var1, Object... var2) {
        return this;
    }

    public PrintStream format(Locale var1, String var2, Object... var3) {
        return this;
    }

    public PrintStream append(CharSequence var1) {
        return this;
    }

    public PrintStream append(CharSequence var1, int var2, int var3) {
        return this;
    }

    public PrintStream append(char var1) {
        return this;
    }
}
