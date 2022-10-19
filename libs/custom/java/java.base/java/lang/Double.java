package java.lang;

public final class Double {
    public static final double POSITIVE_INFINITY = 1.0 / 0.0;
    public static final double NEGATIVE_INFINITY = -1.0 / 0.0;
    public static final double NaN = 0.0d / 0.0;
    public static final double MAX_VALUE = 0x1.fffffffffffffP+1023; // 1.7976931348623157e+308
    public static final double MIN_NORMAL = 0x1.0p-1022; // 2.2250738585072014E-308
    public static final double MIN_VALUE = 0x0.0000000000001P-1022; // 4.9e-324
    public static final int MAX_EXPONENT = 1023;
    public static final int MIN_EXPONENT = -1022;
    public static final int SIZE = 64;
    public static final int BYTES = SIZE / Byte.SIZE;
    public static final Class<Double>   TYPE = (Class<Double>) Class.getPrimitiveClass("double");

    public static String toString(double d) {
        throw new UnsupportedOperationException("");
    }
    public static Double valueOf(String s) throws NumberFormatException {
        return new Double(parseDouble(s));
    }

    public static Double valueOf(double d) {
        // TODO: cache
        return new Double(d);
    }

    public static double parseDouble(String s) throws NumberFormatException {
        throw new UnsupportedOperationException("");
    }

    private final double value;

    public Double(double value) {
        this.value = value;
    }

    public Double(String s) throws NumberFormatException {
        value = parseDouble(s);
    }

    public String toString() {
        return toString(value);
    }

    public byte byteValue() {
        return (byte)value;
    }
    public short shortValue() {
        return (short)value;
    }
    public int intValue() {
        return (int)value;
    }
    public long longValue() {
        return (long)value;
    }
    public float floatValue() {
        return (float)value;
    }
    public double doubleValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
    public static int hashCode(double value) {
        throw new UnsupportedOperationException("");
    }

    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("");
    }

    public static double sum(double a, double b) {
        return a + b;
    }
    public static double max(double a, double b) {
        throw new UnsupportedOperationException("");
    }
    public static double min(double a, double b) {
        throw new UnsupportedOperationException("");
    }
}