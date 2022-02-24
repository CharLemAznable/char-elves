package com.github.charlemaznable.core.lang;

import lombok.NoArgsConstructor;
import net.jodah.expiringmap.ExpiringEntryLoader;
import net.jodah.expiringmap.ExpiringMap;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ExpiringMapp {

    public static <K, V> ExpiringMap<K, V> expiringMap(ExpiringEntryLoader<K, V> loader) {
        return ExpiringMap.builder().expiringEntryLoader(loader).build();
    }
}
