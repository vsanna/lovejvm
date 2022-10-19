package dev.ishikawa.sample;

class Recursive {
    static public void main(String[] args) {
        recursive(1);
    }

    static int recursive(int a) {
        if(a < 1000) {
            return recursive(a + a);
        } else {
            return a;
        }
    }
}