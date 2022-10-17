package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);

    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        throw new UnsupportedOperationException("");
    }

    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        throw new UnsupportedOperationException("");
    }

    static <T> Function<T, T> identity() {
        throw new UnsupportedOperationException("");
    }
}
