package java.lang;

import java.util.Locale;

public final class String {

    private final byte[] value;
    private final byte coder;

    public String() {
        this.value = "".value;
        this.coder = "".coder;
    }

    public String(char value[]) {
        throw new UnsupportedOperationException("");
    }

    public String(char value[], int offset, int count) {
        throw new UnsupportedOperationException("");
    }
    public String(int[] codePoints, int offset, int count) {
        throw new UnsupportedOperationException("");
    }

    public String(byte ascii[], int hibyte, int offset, int count) {
        throw new UnsupportedOperationException("");
    }
    public String(byte ascii[], int hibyte) {
        throw new UnsupportedOperationException("");
    }
    public String(byte bytes[], int offset, int length, String charsetName) {
        throw new UnsupportedOperationException("");
    }

    public String(byte bytes[], String charsetName) {
        this(bytes, 0, bytes.length, charsetName);
    }

    public String(byte bytes[], int offset, int length) {
        throw new UnsupportedOperationException("");
    }

    public int length() {
        return value.length;
    }

    public static String format(String format, Object... args) {
        throw new UnsupportedOperationException("");
    }

    public static String format(Locale l, String format, Object... args) {
        throw new UnsupportedOperationException("");
    }

    public String toUpperCase(Locale locale) {
        throw new UnsupportedOperationException("");
    }

    static final byte LATIN1 = 0;
    static final byte UTF16  = 1;
    static final boolean COMPACT_STRINGS = true;

    boolean isLatin1() {
        return COMPACT_STRINGS && coder == LATIN1;
    }


    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

    public static String valueOf(char data[]) {
        throw new UnsupportedOperationException("");
    }

    public static String valueOf(char data[], int offset, int count) {
        throw new UnsupportedOperationException("");
    }

    static String valueOfCodePoint(int codePoint) {
        throw new UnsupportedOperationException("");
    }

    public char[] toCharArray() {
        throw new UnsupportedOperationException("");
    }
    public String trim() {
        throw new UnsupportedOperationException("");
    }

    public int lastIndexOf(int ch) {
        throw new UnsupportedOperationException("");
    }

    public boolean equalsIgnoreCase(String anotherString) {
        throw new UnsupportedOperationException("");
    }

    public boolean isEmpty() {
        return value.length == 0;
    }

    public char charAt(int index) {
        throw new UnsupportedOperationException("");
    }

    public boolean startsWith(String prefix, int toffset) {
        throw new UnsupportedOperationException("");
    }

    public String substring(int beginIndex) {
        throw new UnsupportedOperationException("");
    }

    public String concat(String str) {
        throw new UnsupportedOperationException("");
    }
    public String repeat(int count) {
        throw new UnsupportedOperationException("");
    }

}
