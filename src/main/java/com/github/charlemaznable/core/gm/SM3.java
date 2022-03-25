package com.github.charlemaznable.core.gm;

import lombok.NoArgsConstructor;
import lombok.val;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import static com.github.charlemaznable.core.codec.Base64.base64;
import static com.github.charlemaznable.core.codec.Bytes.bytes;
import static com.github.charlemaznable.core.codec.Hex.hex;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SM3 extends GM {

    /////////// digest hash ///////////

    public static byte[] digest(byte[] info) {
        val digest = new SM3Digest();
        digest.update(info, 0, info.length);
        val hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }

    public static byte[] digest(String info) {
        return digest(bytes(info));
    }

    public static String digestBase64(byte[] info) {
        return base64(digest(info));
    }

    public static String digestBase64(String info) {
        return digestBase64(bytes(info));
    }

    public static String digestHex(byte[] info) {
        return hex(digest(info));
    }

    public static String digestHex(String info) {
        return digestHex(bytes(info));
    }

    /////////// digest hmac ///////////

    public static byte[] digest(byte[] info, String key) {
        return digest(info, keyParameter(key));
    }

    public static byte[] digest(byte[] info, byte[] key) {
        return digest(info, keyParameter(key));
    }

    public static byte[] digest(byte[] info, CipherParameters key) {
        val digest = new SM3Digest();
        val hMac = new HMac(digest);
        hMac.init(key);
        hMac.update(info, 0, info.length);
        val result = new byte[hMac.getMacSize()];
        hMac.doFinal(result, 0);
        return result;
    }

    public static byte[] digest(String info, String key) {
        return digest(info, keyParameter(key));
    }

    public static byte[] digest(String info, byte[] key) {
        return digest(info, keyParameter(key));
    }

    public static byte[] digest(String info, CipherParameters key) {
        return digest(bytes(info), key);
    }

    public static String digestBase64(byte[] info, String key) {
        return digestBase64(info, keyParameter(key));
    }

    public static String digestBase64(byte[] info, byte[] key) {
        return digestBase64(info, keyParameter(key));
    }

    public static String digestBase64(byte[] info, CipherParameters key) {
        return base64(digest(info, key));
    }

    public static String digestBase64(String info, String key) {
        return digestBase64(info, keyParameter(key));
    }

    public static String digestBase64(String info, byte[] key) {
        return digestBase64(info, keyParameter(key));
    }

    public static String digestBase64(String info, CipherParameters key) {
        return digestBase64(bytes(info), key);
    }

    public static String digestHex(byte[] info, String key) {
        return digestHex(info, keyParameter(key));
    }

    public static String digestHex(byte[] info, byte[] key) {
        return digestHex(info, keyParameter(key));
    }

    public static String digestHex(byte[] info, CipherParameters key) {
        return hex(digest(info, key));
    }

    public static String digestHex(String info, String key) {
        return digestHex(info, keyParameter(key));
    }

    public static String digestHex(String info, byte[] key) {
        return digestHex(info, keyParameter(key));
    }

    public static String digestHex(String info, CipherParameters key) {
        return digestHex(bytes(info), key);
    }

    private static CipherParameters keyParameter(String key) {
        return keyParameter(bytes(key));
    }

    private static CipherParameters keyParameter(byte[] key) {
        return new KeyParameter(key);
    }
}
