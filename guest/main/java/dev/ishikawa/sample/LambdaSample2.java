package dev.ishikawa.sample;

import java.lang.Integer;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.lang.Runnable;

class LambdaSample2 {
    static void main(String[] args) {
        Runnable r1 = () -> {
            System.out.println("hello");
        };

        Runnable r2 = makerun(10L, 100);

        r1.run();
        r2.run();
    }

    static public Runnable makerun(long l, int i) {
        return () -> {
            System.out.println("runnable");
            System.out.println(i);
            System.out.println(l);
        };
    }
}