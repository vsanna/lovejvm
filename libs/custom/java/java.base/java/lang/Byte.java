package java.lang;
public final class Byte {
    public static final byte MIN_VALUE = -128;
    public static final byte MAX_VALUE = 127;
    public static final Class<Byte> TYPE = (Class<Byte>) Class.getPrimitiveClass("byte");
    public static final int SIZE = 8;
    public static final int BYTES = SIZE / Byte.SIZE;
    private final byte value;

    public Byte(byte value) {
        this.value = value;
    }

    public static Byte valueOf(byte b) {
        // TODO: cache
        throw new UnsupportedOperationException("");
    }

    public byte byteValue() {
        return value;
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
        return (double)value;
    }
    public String toString() {
        return Integer.toString((int)value);
    }

    @Override
    public int hashCode() {
        return Byte.hashCode(value);
    }
    public boolean equals(Object obj) {
        if (obj instanceof Byte) {
            return value == ((Byte)obj).byteValue();
        }
        return false;
    }


    public static int hashCode(byte value) {
        return (int)value;
    }

    public static String toString(byte b) {
        return Integer.toString((int)b, 10);
    }

}
