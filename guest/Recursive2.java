class Recursive2 {
    static public void main() {
        fibonacchi(10);
    }

    static int fibonacchi(int n) {
        if(n <= 1) return n;
        return fibonacchi(n-1) + fibonacchi(n-2);
    }
}