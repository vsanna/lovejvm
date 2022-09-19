class Recursive {
    static int recursive(int a) {
        if(a < 10) {
            return recursive(a + 1);
        } else {
            return a;
        }
    }
}