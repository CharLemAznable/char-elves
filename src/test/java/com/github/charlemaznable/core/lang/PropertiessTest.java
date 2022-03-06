package com.github.charlemaznable.core.lang;

import com.github.charlemaznable.core.crypto.AES;
import com.github.charlemaznable.core.crypto.PBE;
import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.codec.Base64.base64;
import static com.github.charlemaznable.core.lang.Propertiess.parseStringToProperties;
import static com.github.charlemaznable.core.lang.Propertiess.tryDecrypt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertiessTest {

    @Test
    public void testPropertiess() {
        val pbePass = base64(PBE.encrypt("sa", "DEFAULT"));
        val aesPass = base64(AES.encrypt("sasa", "DEFAULT"));
        val properties = parseStringToProperties("" +
                "username=auser\n" +
                "password={pbe}" + pbePass + "\n" +
                "password2={aes}" + aesPass + "\n");
        val decrypt = tryDecrypt(properties, "DEFAULT");
        assertEquals("auser", decrypt.getProperty("username"));
        assertEquals("sa", decrypt.getProperty("password"));
        assertEquals("sasa", decrypt.getProperty("password2"));
    }
}
