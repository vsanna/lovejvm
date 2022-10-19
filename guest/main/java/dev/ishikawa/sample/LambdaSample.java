package dev.ishikawa.sample;

import java.util.ArrayList;
import java.util.List;

class LambdaSample {
    static void main(String[] args) {
        Runnable r = () -> System.out.println("hello");

        r.run();
    }
}