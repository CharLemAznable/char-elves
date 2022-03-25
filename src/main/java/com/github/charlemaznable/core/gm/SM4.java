package com.github.charlemaznable.core.gm;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

import static com.github.charlemaznable.core.codec.Bytes.bytes;
import static com.github.charlemaznable.core.codec.Bytes.string;
import static java.lang.System.arraycopy;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SM4 extends GM {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALGORITHM_NAME = "SM4";
    private static final int DEFAULT_KEY_SIZE = 128; // SM4算法目前只支持128位（即密钥16字节）

    public static Key generateKey() {
        return generateKey(DEFAULT_KEY_SIZE);
    }

    @SneakyThrows
    public static Key generateKey(int keySize) {
        val kg = KeyGenerator.getInstance(ALGORITHM_NAME,
                BouncyCastleProvider.PROVIDER_NAME);
        kg.init(keySize, RANDOM);
        return kg.generateKey();
    }

    public static Key buildKey(byte[] key) {
        return buildKey(key, DEFAULT_KEY_SIZE);
    }

    public static Key buildKey(byte[] key, int size) {
        val dstBytes = new byte[size >> 3];

        if (key.length >= dstBytes.length) {
            arraycopy(key, 0, dstBytes, 0, dstBytes.length);
            return new SecretKeySpec(dstBytes, ALGORITHM_NAME);
        }

        int pos = 0;
        while (pos + key.length < dstBytes.length) {
            arraycopy(key, 0, dstBytes, pos, key.length);
            pos += key.length;
        }
        arraycopy(key, 0, dstBytes, pos, dstBytes.length - pos);
        return new SecretKeySpec(dstBytes, ALGORITHM_NAME);
    }

    public static Key buildKey(String key) {
        return buildKey(key, DEFAULT_KEY_SIZE);
    }

    public static Key buildKey(String key, int size) {
        return buildKey(bytes(key), size);
    }

    @AllArgsConstructor
    public enum ECB {

        PKCS5_PADDING("PKCS5Padding"),
        NO_PADDING("NoPadding");

        private String paddingName;

        @SneakyThrows
        public byte[] encrypt(String value, Key key) {
            return cipher(ENCRYPT_MODE, key).doFinal(bytes(value));
        }

        @SneakyThrows
        public String decrypt(byte[] value, Key key) {
            return string(cipher(DECRYPT_MODE, key).doFinal(value));
        }

        public byte[] encrypt(String value, byte[] key) {
            return encrypt(value, buildKey(key));
        }

        public String decrypt(byte[] value, byte[] key) {
            return decrypt(value, buildKey(key));
        }

        public byte[] encrypt(String value, String key) {
            return encrypt(value, buildKey(key));
        }

        public String decrypt(byte[] value, String key) {
            return decrypt(value, buildKey(key));
        }

        @SneakyThrows
        private Cipher cipher(int mode, Key key) {
            val cipher = Cipher.getInstance(cipherName(),
                    BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(mode, key);
            return cipher;
        }

        private String cipherName() {
            return ALGORITHM_NAME + "/ECB/" + paddingName;
        }
    }

    @AllArgsConstructor
    public enum CBC {

        PKCS5_PADDING("PKCS5Padding"),
        NO_PADDING("NoPadding");

        private String paddingName;

        @SneakyThrows
        public byte[] encrypt(String value, Key key, byte[] iv) {
            return cipher(ENCRYPT_MODE, key, iv).doFinal(bytes(value));
        }

        @SneakyThrows
        public String decrypt(byte[] value, Key key, byte[] iv) {
            return string(cipher(DECRYPT_MODE, key, iv).doFinal(value));
        }

        public byte[] encrypt(String value, byte[] key, byte[] iv) {
            return encrypt(value, buildKey(key), iv);
        }

        public String decrypt(byte[] value, byte[] key, byte[] iv) {
            return decrypt(value, buildKey(key), iv);
        }

        public byte[] encrypt(String value, String key, byte[] iv) {
            return encrypt(value, buildKey(key), iv);
        }

        public String decrypt(byte[] value, String key, byte[] iv) {
            return decrypt(value, buildKey(key), iv);
        }

        @SneakyThrows
        private Cipher cipher(int mode, Key key, byte[] iv) {
            val cipher = Cipher.getInstance(cipherName(),
                    BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(mode, key, new IvParameterSpec(iv));
            return cipher;
        }

        private String cipherName() {
            return ALGORITHM_NAME + "/CBC/" + paddingName;
        }
    }
}
