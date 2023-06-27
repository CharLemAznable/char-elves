package com.github.charlemaznable.core.lang.function;

import lombok.NoArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;
import static lombok.Lombok.sneakyThrow;

@NoArgsConstructor(access = PRIVATE)
public final class Unchecker {

    public static <T> Consumer<T> unchecked(ConsumerWithException<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw sneakyThrow(e);
            }
        };
    }

    public static <T, R> Function<T, R> unchecked(FunctionWithException<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw sneakyThrow(e);
            }
        };
    }

    public static <T> Supplier<T> unchecked(SupplierWithException<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw sneakyThrow(e);
            }
        };
    }
}
