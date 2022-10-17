package dev.ishikawa.test;

import java.util.ArrayList;
import java.util.List;

class LambdaSample {
    static void main() {
        Runnable r = () -> System.out.println("hello");

        r.run();
    }
}