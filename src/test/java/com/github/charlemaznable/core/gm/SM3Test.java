package com.github.charlemaznable.core.gm;

import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.codec.Bytes.string;
import static com.github.charlemaznable.core.gm.SM3.digest;
import static com.github.charlemaznable.core.gm.SM3.digestBase64;
import static com.github.charlemaznable.core.gm.SM3.digestHex;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SM3Test {

    private static final byte[] SRC_INFO = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
    private static final String SRC_INFO_STR = string(SRC_INFO);
    private static final byte[] KEY = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
    private static final String KEY_STR = string(KEY);

    @Test
    public void testDigestHash() {
        assertArrayEquals(digest(SRC_INFO), digest(SRC_INFO_STR));
        assertEquals(digestBase64(SRC_INFO), digestBase64(SRC_INFO_STR));
        assertEquals(digestHex(SRC_INFO), digestHex(SRC_INFO_STR));
    }

    @Test
    public void testDigestHMac() {
        assertArrayEquals(digest(SRC_INFO, KEY), digest(SRC_INFO, KEY_STR));
        assertArrayEquals(digest(SRC_INFO_STR, KEY), digest(SRC_INFO_STR, KEY_STR));
        assertEquals(digestBase64(SRC_INFO, KEY), digestBase64(SRC_INFO, KEY_STR));
        assertEquals(digestBase64(SRC_INFO_STR, KEY), digestBase64(SRC_INFO_STR, KEY_STR));
        assertEquals(digestHex(SRC_INFO, KEY), digestHex(SRC_INFO, KEY_STR));
        assertEquals(digestHex(SRC_INFO_STR, KEY), digestHex(SRC_INFO_STR, KEY_STR));
    }
}
