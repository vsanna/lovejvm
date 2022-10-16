package dev.ishikawa.test;

class TypeCast {
    static void main() {
        int i = 100;             // 100
        long i2l = (long) i;     // 0 100
        float i2f = (float) i;   // 42,c8,0,0 == 100.0
        double i2d = (double) i; // 40,59,0,0,0,0,0,0 == 100.0
        byte i2b = (byte) i;     // 100
        char i2c = (char) i;     // 100
        short i2s = (short) i;   // 100
        
        long l = 100L;           // 0 100
        int l2i = (int) l;       // 100
        float l2f = (float) l;   // 42,c8,0,0
        double l2d = (double) l; // 40,59,0,0,0,0,0,0
        byte l2b = (byte) l;     // 100
        char l2c = (char) l;     // 100
        short l2s = (short) l;   // 100

        float f = 1.23F;         // 3f,9d,70,a4 == 1.23
        int f2i = (int) f;       // 1
        long f2l = (long) f;     // 0 1
        double f2d = (double) f; // 3f,f3,ae,14,80,0,0,0 == 1.23
        byte f2b = (byte) f;     // 1
        char f2c = (char) f;     // 1
        short f2s = (short) f;   // 1

        double d = 1.23D;        // 3f,f3,ae,14,7a,e1,47,ae == 1.23
        int d2i = (int) d;       // 1
        long d2l = (long) d;     // 0 1
        float d2f = (float) d;   // 3f,9d,70,a4 == 1.23
        byte d2b = (byte) d;     // 1
        char d2c = (char) d;     // 1
        short d2s = (short) d;   // 1

        byte b = (byte) 100;     // 100
        int b2i = (int) b;       // 100
        long b2l = (long) b;     // 0 100
        float b2f = (float) b;   // 42,c8,0,0
        double b2d = (double) b; // 40,59,0,0,0,0,0,0
        char b2c = (char) b;     // 100
        short b2s = (short) b;   // 100

        char c = 'a';            // 97
        int c2i = (int) c;       // 0 97
        long c2l = (long) c;     // 42,c2,0,0 == 97
        float c2f = (float) c;   // 40,58,40,0,0,0,0,0 == 97
        double c2d = (double) c; // 97
        byte c2b = (byte) c;     // 97
        short c2s = (short) c;   // 97

        short s = 100;           // 100
        int s2i = (int) s;       // 100
        long s2l = (long) s;     // 0 100
        float s2f = (float) s;   // 42,c8,0,0
        double s2d = (double) s; // 40,59,0,0,0,0,0,0
        byte s2b = (byte) s;     // 100
        char s2c = (char) s;     // 100
    }
}