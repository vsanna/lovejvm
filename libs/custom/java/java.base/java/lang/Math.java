package java.lang;

public final class Math {
    private Math() {}

    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }

    public static long max(long a, long b) {
        return (a >= b) ? a : b;
    }

    public static float max(float a, float b) {
        throw new UnsupportedOperationException("");
    }

    public static double max(double a, double b) {
        throw new UnsupportedOperationException("");
    }

    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }

    public static long min(long a, long b) {
        return (a <= b) ? a : b;
    }

    public static float min(float a, float b) {
        throw new UnsupportedOperationException("");
    }

    public static double min(double a, double b) {
        throw new UnsupportedOperationException("");
    }
}
