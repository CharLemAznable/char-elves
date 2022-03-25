package com.github.charlemaznable.core.gm;

import com.github.charlemaznable.core.gm.SM4.CBC;
import com.github.charlemaznable.core.gm.SM4.ECB;
import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.codec.Bytes.string;
import static com.github.charlemaznable.core.gm.SM4.generateKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SM4Test {

    private final String PLAIN_24 = string(new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8});
    private final String PLAIN_48 = string(new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8});
    private final String PLAIN_72 = string(new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8});
    private final String PLAIN_96 = string(new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8});

    @Test
    public void testECB() {
        val key = generateKey().getEncoded();
        val keyStr = string(key);

        byte[] encrypted = ECB.PKCS5_PADDING.encrypt(PLAIN_24, key);
        String decrypted = ECB.PKCS5_PADDING.decrypt(encrypted, key);
        assertEquals(PLAIN_24, decrypted);

        encrypted = ECB.NO_PADDING.encrypt(PLAIN_48, keyStr);
        decrypted = ECB.NO_PADDING.decrypt(encrypted, keyStr);
        assertEquals(PLAIN_48, decrypted);
    }

    @Test
    public void testCBC() {
        val key = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
        val keyStr = string(key);
        val iv = generateKey().getEncoded();

        byte[] encrypted = CBC.PKCS5_PADDING.encrypt(PLAIN_24, key, iv);
        String decrypted = CBC.PKCS5_PADDING.decrypt(encrypted, key, iv);
        assertEquals(PLAIN_24, decrypted);

        encrypted = CBC.NO_PADDING.encrypt(PLAIN_48, keyStr, iv);
        decrypted = CBC.NO_PADDING.decrypt(encrypted, keyStr, iv);
        assertEquals(PLAIN_48, decrypted);
    }
}
