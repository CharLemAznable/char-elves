package com.github.charlemaznable.core.lang;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NoArgsConstructor;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class YCombinator {

    public static <T, R> Function<T, R> of(UnaryOperator<Function<T, R>> f) {
        return n -> f.apply(of(f)).apply(n);
    }

    public static <T, R> Function<T, R> of(CacheableUnaryOperator<T, R> f) {
        return n -> nullThen(f.getIfPresent(n), () -> f.put(n, f.apply(of(f)).apply(n)));
    }

    public abstract static class CacheableUnaryOperator<T, R>
            implements UnaryOperator<Function<T, R>> {

        private final Cache<T, R> cache = CacheBuilder.newBuilder().build();

        public R getIfPresent(T key) {
            return cache.getIfPresent(key);
        }

        public R put(T key, R value) {
            cache.put(key, value);
            return value;
        }
    }
}
