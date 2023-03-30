package dev.ishikawa.sample;

class Math {
    static void main(String[] args) {
        int i1 = 100 + 200; // 300
        int i2 = 100 - 200; // -100
        int i3 = 100 * 200; // 20000
        int i4 = 200 / 99;  // 2
        int i5 = 100 % 200; // 100
        int i6 = 100 & 200; // 64
        int i7 = 100 | 200; // 236
        int i8 = 100;
        i8++;               // 101

        long l1 = 100L + 200L; // 0 300
        long l2 = 100L - 200L; // -1 -100 == FFFFFFFFFFFFFF9C
        long l3 = 100L * 200L; // 0 20000
        long l4 = 200L / 99L;  // 0 2
        long l5 = 100L % 200L; // 0 100
        long l6 = 100L & 200L; // 0 64
        long l7 = 100L | 200L; // 0 236
        long l8 = 100L;
        l8++;
        l8 = l8 + 1000;        // 1101

        float f1 = 1.23f + 3.21f; // 4.44 == 40 8e 14 7b
        float f2 = 1.23f - 3.21f; // -1.98 == bf fd 70 a4
        float f3 = 1.23f * 3.21f; // 3.9483 == 40 7c b0 f2
        float f4 = 1.23f / 3.21f; // 0.38317757 == 3e c4 2f da
        float f5 = 1.23f % 3.21f; // 1.23 == 3f 9d 70 a4

        double d1 = 1.23D + 3.21D; // 40 11 c2 8f 5c 28 f5 c3
        double d2 = 1.23D - 3.21D; // bf ff ae 14 7a e1 47 ae
        double d3 = 1.23D * 3.21D; // 40 0f 96 1e 4f 76 5f d9
        double d4 = 1.23D / 3.21D; // 3f d8 85 fb 36 ed 7c f1
        double d5 = 1.23D % 3.21D; // 3f f3 ae 14 7a e1 47 ae

        short s1 = ((short) 100) + ((short) 200); // 300
        short s2 = ((short) 100) - ((short) 200); // 156
        short s3 = ((short) 100) * ((short) 200); // 20000
        short s4 = ((short) 100) / ((short) 200); // 0
        short s5 = ((short) 100) % ((short) 200); // 100

        char c1 = ((char) 100) + ((char) 200); // 300
        char c2 = ((char) 200) - ((char) 100); // 100
        char c3 = ((char) 100) * ((char) 200); // 20000
        char c4 = ((char) 100) / ((char) 200); // 0
        char c5 = ((char) 100) % ((char) 200); // 100

        byte b1 = ((byte) 10) + ((byte) 20); // 30
        byte b2 = ((byte) 10) - ((byte) 20); // 246
        byte b3 = ((byte) 10) * ((byte) 5);  // 50
        byte b4 = ((byte) 10) / ((byte) 20); // 0
        byte b5 = ((byte) 10) % ((byte) 20); // 10
    }
}