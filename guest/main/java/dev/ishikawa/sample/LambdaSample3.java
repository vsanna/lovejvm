package dev.ishikawa.sample;

import java.lang.Integer;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.lang.Runnable;

class LambdaSample3 {
    static void main(String[] args) {
        Runnable r1 = () -> {
            System.out.println("hello");
        };
        r1.run();

        Runnable r2 = makerun(10L, 100);
        r2.run();
        System.out.println("==========");

        Consumer r3 = makeConsumer();
        r3.accept(321);
        System.out.println("==========");

        Supplier r4 = makeSupplier(123);
        System.out.println(r4.get());
        System.out.println("==========");

        Function r5 = makeFunction(1000);
        System.out.println(r5.apply(-2000));
        System.out.println("==========");

        Function r6 = makeFunction2(1234);
        System.out.println(r6.apply(-2345));
        System.out.println("==========");

        BiFunction r7 = makeBiFunction(10);
        System.out.println(r7.apply(-50, 20));
        System.out.println("==========");

        BiConsumer r8 = makeBiConsumer();
        r8.accept(-50, 20);
        System.out.println("==========");
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

    static public <P, R> Function<P, R> makeFunction(R ret) {
        return (P param) -> {
            System.out.println("function");
            System.out.println(param);
            return ret;
        };
    }

    static public <P, R> Function<P, R> makeFunction2(R ret) {
        String strToCapture = "capture me!";
        long longToCapture = 123L;
        boolean boolToCapture = true;
        double doubleToCapture = 1.23D;

        return (P param) -> { // this == 357, param == 359
            System.out.println("function");
            System.out.println(longToCapture);  // cap1
            System.out.println(strToCapture);   // cap2 ... 341: String
            System.out.println(param);          // cap5 ... Integer
            System.out.println(boolToCapture);  // cap3
            System.out.println(doubleToCapture);// cap4
            return ret;
        };
    }

    static public <P, R> BiFunction<P, P, R> makeBiFunction(R ret) {
        return (P p1, P p2) -> {
            System.out.println("bifunction");
            System.out.println(p1);
            System.out.println(p2);
            return ret;
        };
    }

    static public <P> BiConsumer<P, P> makeBiConsumer() {
        return (P p1, P p2) -> {
            System.out.println("biconsumer");
            System.out.println(p1);
            System.out.println(p2);
        };
    }
}
