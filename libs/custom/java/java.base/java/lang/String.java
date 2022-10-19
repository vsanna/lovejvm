package java.lang;

public final class String {

    private final byte[] value;
    private final byte coder;

    static final byte UTF16  = 1;

    public String() {
        this.value = "".value;
        this.coder = "".coder;
    }

    public String(byte[] value) {
        if(value.length == 0) {
            this.value = "".value;
            this.coder = "".coder;
            return;
        }

        this.value = value;
        this.coder = UTF16;
    }

    public String(char[] value) {
        if(value.length == 0) {
            this.value = "".value;
            this.coder = "".coder;
            return;
        }
        byte[] bytes = new byte[value.length];
        for (int i = 0; i < value.length; i++) {
            bytes[i] = (byte)value[i];
        }

        this.value = bytes;
        this.coder = UTF16;
    }

    public int length() {
        return value.length;
    }
    
    public static String valueOf(int value) { return Integer.toString(value); }
    public static String valueOf(long value) { return Long.toString(value); }
    public static String valueOf(short value) { return Short.toString(value); }
    public static String valueOf(byte value) { return Byte.toString(value); }
    public static String valueOf(float value) { return Float.toString(value); }
    public static String valueOf(double value) { return Double.toString(value); }
    public static String valueOf(boolean value) { return Boolean.toString(value); }
    public static String valueOf(char value) { return Character.toString(value); }
    public static String valueOf(Object obj) { return (obj == null) ? "null" : obj.toString(); }

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

    public native String intern();
}
