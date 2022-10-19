package java.lang;

public final class Long {
    public static final long MIN_VALUE = 0x8000000000000000L;
    public static final long MAX_VALUE = 0x7fffffffffffffffL;
    public static final Class<Long> TYPE = (Class<Long>) Class.getPrimitiveClass("long");
    public static final int SIZE = 64;
    public static final int BYTES = SIZE / Byte.SIZE;


    private final long value;

    public Long(long value) {
        this.value = value;
    }

    public static Long valueOf(long l) {
        // TODO: cache
        throw new UnsupportedOperationException("");
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
        return value;
    }
    public float floatValue() {
        return (float)value;
    }
    public double doubleValue() {
        return (double)value;
    }
    public String toString() {
        return toString(value);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Long) {
            return value == ((Long)obj).longValue();
        }
        return false;
    }


    public static int hashCode(long value) {
        return (int)(value ^ (value >>> 32));
    }

    public static String toString(long l) {
        if(l == 0) {
            return "0";
        }
        if(l == Long.MAX_VALUE) {
            return "9223372036854775807";
        }
        if(l == Long.MIN_VALUE) {
            return "-9223372036854775808";
        }

        boolean isNegative = l < 0;
        int digit = 0;
        char[] nums = new char[20];

        l = isNegative ? (-1 * l) : l;

        while(l >= 10) {
            long a = l%10;
            nums[digit] = Integer.NumChars[(int)a];
            digit++;
            l = (l-a)/10;
        }
        nums[digit] = Integer.NumChars[(int)l];

        char[] reversedNums = new char[isNegative ? digit + 2 : digit + 1];

        if(isNegative) {
            reversedNums[0] = '-';
        }

        for (int i1 = 0; i1 <= digit; i1++) {
            reversedNums[isNegative ? i1 + 1 : i1] = nums[digit - i1];
        }

        return new String(reversedNums);
    }

    public static long sum(long a, long b) {
        return a + b;
    }

    public static long max(long a, long b) {
        throw new UnsupportedOperationException("");
    }

    public static long min(long a, long b) {
        throw new UnsupportedOperationException("");
    }
}