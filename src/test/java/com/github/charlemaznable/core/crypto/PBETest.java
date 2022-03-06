package com.github.charlemaznable.core.crypto;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.codec.Base64.base64;
import static com.github.charlemaznable.core.codec.Base64.unBase64;
import static com.github.charlemaznable.core.crypto.PBE.decrypt;
import static com.github.charlemaznable.core.crypto.PBE.encrypt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PBETest {

    @Test
    public void testPBE() {
        val originalPassword = "secret";
        val encryptedPassword = base64(encrypt(originalPassword, "mypass"));
        val decryptedPassword = decrypt(unBase64(encryptedPassword), "mypass");
        assertEquals(originalPassword, decryptedPassword);
    }
}
