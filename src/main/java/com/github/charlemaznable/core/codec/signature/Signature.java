package com.github.charlemaznable.core.codec.signature;

import com.github.charlemaznable.core.codec.Digest;
import com.github.charlemaznable.core.codec.DigestHMAC;
import com.github.charlemaznable.core.codec.Json;
import com.github.charlemaznable.core.crypto.SHAXWithRSA;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.github.charlemaznable.core.lang.Condition.notNullThen;
import static com.github.charlemaznable.core.lang.Mapp.newHashMap;
import static com.github.charlemaznable.core.lang.Str.toStr;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
public final class Signature {

    private String key;
    private String value;

    ////////////////////////////////////////////////////////////////

    public static Signature signature(Map<String, Object> source) {
        return signature(source, new SignatureOptions());
    }

    public static boolean verify(Map<String, Object> source) {
        return verify(source, new SignatureOptions());
    }

    public static Signature signature(String key, Map<String, Object> source) {
        return signature(source, new SignatureOptions().key(key));
    }

    public static boolean verify(String key, Map<String, Object> source) {
        return verify(source, new SignatureOptions().key(key));
    }

    ////////////////////////////////////////////////////////////////

    public static Signature signatureDigestBase64(Map<String, Object> source, Digest digest) {
        return signature(source, new SignatureOptions()
                .signAlgorithm(digest::digestBase64));
    }

    public static boolean verifyDigestBase64(Map<String, Object> source, Digest digest) {
        return verify(source, new SignatureOptions()
                .verifyAlgorithm((p, s) -> digest.digestBase64(p).equals(s)));
    }

    public static Signature signatureDigestBase64(String key, Map<String, Object> source, Digest digest) {
        return signature(source, new SignatureOptions().key(key)
                .signAlgorithm(digest::digestBase64));
    }

    public static boolean verifyDigestBase64(String key, Map<String, Object> source, Digest digest) {
        return verify(source, new SignatureOptions().key(key)
                .verifyAlgorithm((p, s) -> digest.digestBase64(p).equals(s)));
    }

    ////////////////////////////////////////////////////////////////

    public static Signature signatureDigestHex(Map<String, Object> source, Digest digest) {
        return signature(source, new SignatureOptions()
                .signAlgorithm(digest::digestHex));
    }

    public static boolean verifyDigestHex(Map<String, Object> source, Digest digest) {
        return verify(source, new SignatureOptions()
                .verifyAlgorithm((p, s) -> digest.digestHex(p).equalsIgnoreCase(s)));
    }

    public static Signature signatureDigestHex(String key, Map<String, Object> source, Digest digest) {
        return signature(source, new SignatureOptions().key(key)
                .signAlgorithm(digest::digestHex));
    }

    public static boolean verifyDigestHex(String key, Map<String, Object> source, Digest digest) {
        return verify(source, new SignatureOptions().key(key)
                .verifyAlgorithm((p, s) -> digest.digestHex(p).equalsIgnoreCase(s)));
    }

    ////////////////////////////////////////////////////////////////

    public static Signature signatureDigestHMACBase64
            (Map<String, Object> source, DigestHMAC digestHMAC, byte[] digestKey) {
        return signature(source, new SignatureOptions()
                .signAlgorithm(p -> digestHMAC.digestBase64(p, digestKey)));
    }

    public static boolean verifyDigestHMACBase64
            (Map<String, Object> source, DigestHMAC digestHMAC, byte[] digestKey) {
        return verify(source, new SignatureOptions()
                .verifyAlgorithm((p, s) -> digestHMAC.digestBase64(p, digestKey).equals(s)));
    }

    public static Signature signatureDigestHMACBase64
            (String key, Map<String, Object> source, DigestHMAC digestHMAC, byte[] digestKey) {
        return signature(source, new SignatureOptions().key(key)
                .signAlgorithm(p -> digestHMAC.digestBase64(p, digestKey)));
    }

    public static boolean verifyDigestHMACBase64
            (String key, Map<String, Object> source, DigestHMAC digestHMAC, byte[] digestKey) {
        return verify(source, new SignatureOptions().key(key)
                .verifyAlgorithm((p, s) -> digestHMAC.digestBase64(p, digestKey).equals(s)));
    }

    ////////////////////////////////////////////////////////////////

    public static Signature signatureDigestHMACHex
            (Map<String, Object> source, DigestHMAC digestHMAC, byte[] digestKey) {
        return signature(source, new SignatureOptions()
                .signAlgorithm(p -> digestHMAC.digestHex(p, digestKey)));
    }

    public static boolean verifyDigestHMACHex
            (Map<String, Object> source, DigestHMAC digestHMAC, byte[] digestKey) {
        return verify(source, new SignatureOptions()
                .verifyAlgorithm((p, s) -> digestHMAC.digestHex(p, digestKey).equalsIgnoreCase(s)));
    }

    public static Signature signatureDigestHMACHex
            (String key, Map<String, Object> source, DigestHMAC digestHMAC, byte[] digestKey) {
        return signature(source, new SignatureOptions().key(key)
                .signAlgorithm(p -> digestHMAC.digestHex(p, digestKey)));
    }

    public static boolean verifyDigestHMACHex
            (String key, Map<String, Object> source, DigestHMAC digestHMAC, byte[] digestKey) {
        return verify(source, new SignatureOptions().key(key)
                .verifyAlgorithm((p, s) -> digestHMAC.digestHex(p, digestKey).equalsIgnoreCase(s)));
    }

    ////////////////////////////////////////////////////////////////

    public static Signature signatureSHAWithRSABase64
            (Map<String, Object> source, SHAXWithRSA shaxWithRSA, String privateKey) {
        return signature(source, new SignatureOptions()
                .signAlgorithm(p -> shaxWithRSA.signBase64(p, privateKey)));
    }

    public static boolean verifySHAWithRSABase64
            (Map<String, Object> source, SHAXWithRSA shaxWithRSA, String publicKey) {
        return verify(source, new SignatureOptions()
                .verifyAlgorithm((p, s) -> shaxWithRSA.verifyBase64(p, s, publicKey)));
    }

    public static Signature signatureSHAWithRSABase64
            (String key, Map<String, Object> source, SHAXWithRSA shaxWithRSA, String privateKey) {
        return signature(source, new SignatureOptions().key(key)
                .signAlgorithm(p -> shaxWithRSA.signBase64(p, privateKey)));
    }

    public static boolean verifySHAWithRSABase64
            (String key, Map<String, Object> source, SHAXWithRSA shaxWithRSA, String publicKey) {
        return verify(source, new SignatureOptions().key(key)
                .verifyAlgorithm((p, s) -> shaxWithRSA.verifyBase64(p, s, publicKey)));
    }

    ////////////////////////////////////////////////////////////////

    public static Signature signatureSHAWithRSAHex
            (Map<String, Object> source, SHAXWithRSA shaxWithRSA, String privateKey) {
        return signature(source, new SignatureOptions()
                .signAlgorithm(p -> shaxWithRSA.signHex(p, privateKey)));
    }

    public static boolean verifySHAWithRSAHex
            (Map<String, Object> source, SHAXWithRSA shaxWithRSA, String publicKey) {
        return verify(source, new SignatureOptions()
                .verifyAlgorithm((p, s) -> shaxWithRSA.verifyHex(p, s, publicKey)));
    }

    public static Signature signatureSHAWithRSAHex
            (String key, Map<String, Object> source, SHAXWithRSA shaxWithRSA, String privateKey) {
        return signature(source, new SignatureOptions().key(key)
                .signAlgorithm(p -> shaxWithRSA.signHex(p, privateKey)));
    }

    public static boolean verifySHAWithRSAHex
            (String key, Map<String, Object> source, SHAXWithRSA shaxWithRSA, String publicKey) {
        return verify(source, new SignatureOptions().key(key)
                .verifyAlgorithm((p, s) -> shaxWithRSA.verifyHex(p, s, publicKey)));
    }

    ////////////////////////////////////////////////////////////////

    public static Signature signature(Map<String, Object> source, SignatureOptions options) {
        return new Signature(options.key(), options
                .signAlgorithm().apply(buildPlain(source, options)));
    }

    public static boolean verify(Map<String, Object> source, SignatureOptions options) {
        return options.verifyAlgorithm().test(buildPlain(
                source, options), toStr(source.get(options.key())));
    }

    ////////////////////////////////////////////////////////////////

    private static String buildPlain(Map<String, Object> source, SignatureOptions options) {
        Map<String, String> flatMap;
        if (options.flatValue()) {
            flatMap = Json.descFlat(source);
        } else {
            flatMap = newHashMap();
            source.forEach((k, v) -> flatMap.put(
                    k, notNullThen(v, Object::toString)));
        }
        val tempMap = options.keySortAsc() ? new TreeMap<>(flatMap)
                : new TreeMap<>(flatMap).descendingMap();
        tempMap.remove(options.key());
        return options.plainProcessor().apply(tempMap
                .entrySet().stream()
                .filter(options.entryFilter())
                .map(options.entryMapper())
                .collect(Collectors.joining(options.entrySeparator())));
    }
}
