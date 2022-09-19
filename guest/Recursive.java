class Recursive {
    static public void recursiveMain() {
        recursive(10);
    }

    static int recursive(int a) {
        if(a < 1000) {
            return recursive(a + a);
        } else {
            return a;
        }
    }
}