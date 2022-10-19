package dev.ishikawa.sample;

class ForLoop {
    static public void main(String[] args) {
        int a = 0;
        for(int i = 0; i < 10000; i++) {
            a += i;
        }
        return;
    }
}