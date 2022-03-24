package com.github.charlemaznable.core.gm;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bouncycastle.crypto.engines.SM2Engine.Mode;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.codec.Bytes.string;
import static com.github.charlemaznable.core.gm.SM2.decrypt;
import static com.github.charlemaznable.core.gm.SM2.encrypt;
import static com.github.charlemaznable.core.gm.SM2.generateKeyPair;
import static com.github.charlemaznable.core.gm.SM2.generateKeyPairParameter;
import static com.github.charlemaznable.core.gm.SM2.getPrivateKey;
import static com.github.charlemaznable.core.gm.SM2.getPrivateKeyParameters;
import static com.github.charlemaznable.core.gm.SM2.getPublicKey;
import static com.github.charlemaznable.core.gm.SM2.getPublicKeyParameters;
import static com.github.charlemaznable.core.gm.SM2.sign;
import static com.github.charlemaznable.core.gm.SM2.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class SM2Test {

    private final String PLAIN_24 = string(new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8});
    private final String PLAIN_48 = string(new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8});
    private final String PLAIN_72 = string(new byte[]{
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8,
            1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8});
    private final String SIGN_ID = string(new byte[]{1, 2, 3, 4});

    @Test
    public void testEncryptAndDecrypt() {
        val keyPair = generateKeyPair();
        val publicKey = getPublicKey(keyPair);
        val privateKey = getPrivateKey(keyPair);

        byte[] encrypted = encrypt(publicKey, PLAIN_24);
        String decrypted = decrypt(privateKey, encrypted);
        assertEquals(PLAIN_24, decrypted);
    }

    @Test
    public void testEncryptAndDecryptC1C2C3() {
        val keyPair = generateKeyPair();
        val publicKey = getPublicKey(keyPair);
        val privateKey = getPrivateKey(keyPair);

        byte[] encrypted = encrypt(Mode.C1C2C3, publicKey, PLAIN_48);
        String decrypted = decrypt(Mode.C1C2C3, privateKey, encrypted);
        assertEquals(PLAIN_48, decrypted);
    }

    @Test
    public void testEncryptAndDecryptParameters() {
        val keyPair = generateKeyPairParameter();
        val publicKeyParameters = getPublicKeyParameters(keyPair);
        val privateKeyParameters = getPrivateKeyParameters(keyPair);

        byte[] encrypted = encrypt(publicKeyParameters, PLAIN_72);
        String decrypted = decrypt(privateKeyParameters, encrypted);
        assertEquals(PLAIN_72, decrypted);
    }

    @Test
    public void testSignAndVerify() {
        val keyPair = generateKeyPair();
        val publicKey = getPublicKey(keyPair);
        val privateKey = getPrivateKey(keyPair);

        byte[] sign = sign(privateKey, PLAIN_24);
        assertTrue(verify(publicKey, PLAIN_24, sign));
    }

    @Test
    public void testSignAndVerifyWithId() {
        val keyPair = generateKeyPair();
        val publicKey = getPublicKey(keyPair);
        val privateKey = getPrivateKey(keyPair);

        byte[] sign = sign(privateKey, SIGN_ID, PLAIN_48);
        assertTrue(verify(publicKey, SIGN_ID, PLAIN_48, sign));
    }

    @Test
    public void testSignAndVerifyParameters() {
        val keyPair = generateKeyPairParameter();
        val publicKeyParameters = getPublicKeyParameters(keyPair);
        val privateKeyParameters = getPrivateKeyParameters(keyPair);

        byte[] sign = sign(privateKeyParameters, PLAIN_72);
        assertTrue(verify(publicKeyParameters, PLAIN_72, sign));
    }
}
