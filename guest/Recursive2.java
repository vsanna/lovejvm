package dev.ishikawa.test;

class Recursive2 {
    static public void main() {
//        fibonacchi(10);  // 55
        fibonacchi(13);  // 233
//        fibonacchi(40);  // 102334155
//        fibonacchi(100); // 354224848179261915075
    }

    static int fibonacchi(int n) {
        if(n <= 1) return n;
        return fibonacchi(n-1) + fibonacchi(n-2);
    }
}