package dev.ishikawa.sample;

import java.lang.Integer;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.lang.Runnable;

class LambdaSample {
    static void main(String[] args) {
        makerun(100).run();
//        makeConsumer().accpet(200);
//        System.out.println(makeSupplier<Integer>(300).get());
    }

    static public Runnable makerun(int i) {
        return () -> {
            System.out.println("runnable");
            System.out.println(i);
        };
    }
//
//    static public Consumer<Integer> makeConsumer() {
//        return (int a) -> {
//            System.out.println("consumer");
//            System.out.println(a);
//        };
//    }
//
//    static public <T> Supplier<T> makeSupplier(int i) {
//        return () -> {
//            System.out.println("supplier");
//            return i;
//        };
//    }
}