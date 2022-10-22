package dev.ishikawa.sample;

import java.lang.Integer;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.lang.Runnable;

class LambdaSample {
    static void main(String[] args) {
        Runnable r1 = () -> { System.out.println("runnable 1!"); };
        Runnable r2 = () -> { System.out.println("runnable 2!"); };
        System.out.println("starting!");
        r1.run();
        r2.run();
    }
}