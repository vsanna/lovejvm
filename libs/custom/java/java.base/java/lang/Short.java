package java.lang;

public final class Short {
    public static final short   MIN_VALUE = -32768;
    public static final short   MAX_VALUE = 32767;
    public static final Class<Short>    TYPE = (Class<Short>) Class.getPrimitiveClass("short");

    public static String toString(short s) {
        return Integer.toString((int)s, 10);
    }

    public static short parseShort(String s, int radix) {
        throw new UnsupportedOperationException("");
    }

    public static short parseShort(String s) throws NumberFormatException {
        return parseShort(s, 10);
    }

    public static Short valueOf(String s, int radix)
        throws NumberFormatException {
        return valueOf(parseShort(s, radix));
    }

    public static Short valueOf(String s) throws NumberFormatException {
        return valueOf(s, 10);
    }

    public static Short valueOf(short s) {
        throw new UnsupportedOperationException("");
    }

    public static Short decode(String nm) throws NumberFormatException {
        throw new UnsupportedOperationException("");
    }

    private final short value;

    public Short(short value) {
        this.value = value;
    }

    public Short(String s) throws NumberFormatException {
        this.value = parseShort(s, 10);
    }

    public byte byteValue() {
        return (byte)value;
    }
    public short shortValue() {
        return value;
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
        return Short.hashCode(value);
    }

    public static int hashCode(short value) {
        return (int)value;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Short) {
            return value == ((Short)obj).shortValue();
        }
        return false;
    }
//
//    /**
//     * Compares two {@code Short} objects numerically.
//     *
//     * @param   anotherShort   the {@code Short} to be compared.
//     * @return  the value {@code 0} if this {@code Short} is
//     *          equal to the argument {@code Short}; a value less than
//     *          {@code 0} if this {@code Short} is numerically less
//     *          than the argument {@code Short}; and a value greater than
//     *           {@code 0} if this {@code Short} is numerically
//     *           greater than the argument {@code Short} (signed
//     *           comparison).
//     * @since   1.2
//     */
//    public int compareTo(Short anotherShort) {
//        return compare(this.value, anotherShort.value);
//    }
//
//    /**
//     * Compares two {@code short} values numerically.
//     * The value returned is identical to what would be returned by:
//     * <pre>
//     *    Short.valueOf(x).compareTo(Short.valueOf(y))
//     * </pre>
//     *
//     * @param  x the first {@code short} to compare
//     * @param  y the second {@code short} to compare
//     * @return the value {@code 0} if {@code x == y};
//     *         a value less than {@code 0} if {@code x < y}; and
//     *         a value greater than {@code 0} if {@code x > y}
//     * @since 1.7
//     */
//    public static int compare(short x, short y) {
//        return x - y;
//    }
//
//    /**
//     * Compares two {@code short} values numerically treating the values
//     * as unsigned.
//     *
//     * @param  x the first {@code short} to compare
//     * @param  y the second {@code short} to compare
//     * @return the value {@code 0} if {@code x == y}; a value less
//     *         than {@code 0} if {@code x < y} as unsigned values; and
//     *         a value greater than {@code 0} if {@code x > y} as
//     *         unsigned values
//     * @since 9
//     */
//    public static int compareUnsigned(short x, short y) {
//        return Short.toUnsignedInt(x) - Short.toUnsignedInt(y);
//    }

    /**
     * The number of bits used to represent a {@code short} value in two's
     * complement binary form.
     * @since 1.5
     */
    public static final int SIZE = 16;

    /**
     * The number of bytes used to represent a {@code short} value in two's
     * complement binary form.
     *
     * @since 1.8
     */
    public static final int BYTES = SIZE / Byte.SIZE;
//
//    /**
//     * Returns the value obtained by reversing the order of the bytes in the
//     * two's complement representation of the specified {@code short} value.
//     *
//     * @param i the value whose bytes are to be reversed
//     * @return the value obtained by reversing (or, equivalently, swapping)
//     *     the bytes in the specified {@code short} value.
//     * @since 1.5
//     */
//    public static short reverseBytes(short i) {
//        return (short) (((i & 0xFF00) >> 8) | (i << 8));
//    }
//
//
//    /**
//     * Converts the argument to an {@code int} by an unsigned
//     * conversion.  In an unsigned conversion to an {@code int}, the
//     * high-order 16 bits of the {@code int} are zero and the
//     * low-order 16 bits are equal to the bits of the {@code short} argument.
//     *
//     * Consequently, zero and positive {@code short} values are mapped
//     * to a numerically equal {@code int} value and negative {@code
//     * short} values are mapped to an {@code int} value equal to the
//     * input plus 2<sup>16</sup>.
//     *
//     * @param  x the value to convert to an unsigned {@code int}
//     * @return the argument converted to {@code int} by an unsigned
//     *         conversion
//     * @since 1.8
//     */
//    public static int toUnsignedInt(short x) {
//        return ((int) x) & 0xffff;
//    }
//
//    /**
//     * Converts the argument to a {@code long} by an unsigned
//     * conversion.  In an unsigned conversion to a {@code long}, the
//     * high-order 48 bits of the {@code long} are zero and the
//     * low-order 16 bits are equal to the bits of the {@code short} argument.
//     *
//     * Consequently, zero and positive {@code short} values are mapped
//     * to a numerically equal {@code long} value and negative {@code
//     * short} values are mapped to a {@code long} value equal to the
//     * input plus 2<sup>16</sup>.
//     *
//     * @param  x the value to convert to an unsigned {@code long}
//     * @return the argument converted to {@code long} by an unsigned
//     *         conversion
//     * @since 1.8
//     */
//    public static long toUnsignedLong(short x) {
//        return ((long) x) & 0xffffL;
//    }
}
