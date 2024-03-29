package com.github.charlemaznable.core.lang;

import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Empty {

    public static boolean isEmpty(Object obj) {
        if (obj instanceof CharSequence charSequence) {
            return charSequence.length() == 0;
        } else if (obj instanceof Collection collection) {
            return collection.isEmpty();
        } else if (obj instanceof Map map) {
            return map.isEmpty();
        } else {
            return isNull(obj);
        }
    }
}
