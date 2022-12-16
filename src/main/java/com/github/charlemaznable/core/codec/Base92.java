package com.github.charlemaznable.core.codec;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Base92 {

    private static final BaseX baseX92 = new BaseX(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz`1234567890-=~!@#$%^&*()_+[]{}|;':,./<>?");

    public static String base92(byte[] bytes) {
        return baseX92.encode(bytes);
    }

    public static byte[] unBase92(String value) {
        return baseX92.decode(value);
    }
}
