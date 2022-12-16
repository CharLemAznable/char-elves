package com.github.charlemaznable.core.lang;

import lombok.NoArgsConstructor;
import net.jodah.expiringmap.ExpiringEntryLoader;
import net.jodah.expiringmap.ExpiringValue;

import java.util.function.Function;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ExpiringEntryLoaderr {

    public static <K, V> ExpiringEntryLoader<K, V> from(Function<K, ExpiringValue<V>> function) {
        return new FunctionToExpiringEntryLoader<>(function);
    }

    private record FunctionToExpiringEntryLoader<K, V>(
            Function<K, ExpiringValue<V>> computingFunction) implements ExpiringEntryLoader<K, V> {

        private FunctionToExpiringEntryLoader(Function<K, ExpiringValue<V>> computingFunction) {
            this.computingFunction = checkNotNull(computingFunction);
        }

        public ExpiringValue<V> load(K key) {
            return this.computingFunction.apply(checkNotNull(key));
        }
    }
}
