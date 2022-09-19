class ForLoop {
    static public int test() {
        int a = 0;
        for(int i = 0; i < 10000; i++) {
            a += i;
        }
        return a;
    }
}