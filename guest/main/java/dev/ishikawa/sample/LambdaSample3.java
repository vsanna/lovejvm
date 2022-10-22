package dev.ishikawa.sample;

import java.lang.Integer;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.lang.Runnable;

class LambdaSample3 {
    static void main(String[] args) {
        Runnable r1 = () -> {
            System.out.println("hello");
        };
        r1.run();

        Runnable r2 = makerun(10L, 100);
        r2.run();

        Consumer r3 = makeConsumer();
        r3.accept(321);

        Supplier r4 = makeSupplier(123);
        System.out.println(r4.get());
    }

    static public Runnable makerun(long l, int i) {
        return () -> {
            System.out.println("runnable");
            System.out.println(i);
            System.out.println(l);
        };
    }

    static public <T> Consumer<T> makeConsumer() {
        String test = "this is test!";
        return (T a) -> {
            System.out.println("consumer");
            System.out.println(a);
            System.out.println(test);
        };
    }

    static public <T> Supplier<T> makeSupplier(T i) {
        return () -> {
            System.out.println("supplier");
            return i;
        };
    }
}