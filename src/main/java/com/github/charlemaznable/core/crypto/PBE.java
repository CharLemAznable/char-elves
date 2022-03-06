package com.github.charlemaznable.core.crypto;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import static com.github.charlemaznable.core.codec.Bytes.bytes;
import static com.github.charlemaznable.core.codec.Bytes.string;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class PBE {

    private static final byte[] SALT = {
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };

    private static final String ALGORITHM = "PBEWITHMD5andDES";

    @SneakyThrows
    public static byte[] encrypt(String value, String password) {
        val keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        val key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
        val pbeCipher = Cipher.getInstance(ALGORITHM);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return pbeCipher.doFinal(bytes(value));
    }

    @SneakyThrows
    public static String decrypt(byte[] value, String password) {
        val keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        val key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
        val pbeCipher = Cipher.getInstance(ALGORITHM);
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return string(pbeCipher.doFinal(value));
    }
}
