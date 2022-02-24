package com.github.charlemaznable.core.lang;

import lombok.NoArgsConstructor;
import org.joor.ReflectException;

import java.lang.reflect.Field;

import static lombok.AccessLevel.PRIVATE;
import static org.joor.Reflect.accessible;

@NoArgsConstructor(access = PRIVATE)
public final class Reflectt {

    public static Field field0(Class<?> clazz, String fieldName) {
        Class<?> t = clazz;
        try {
            return accessible(t.getField(fieldName));
        } catch (NoSuchFieldException e) {
            do {
                try {
                    return accessible(t.getDeclaredField(fieldName));
                } catch (NoSuchFieldException ignore) {
                    // ignored
                }
                t = t.getSuperclass();
            } while (t != null);
            throw new ReflectException(e);
        }
    }
}
