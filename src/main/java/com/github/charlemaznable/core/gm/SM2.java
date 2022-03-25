package com.github.charlemaznable.core.gm;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.engines.SM2Engine.Mode;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

import static com.github.charlemaznable.core.codec.Bytes.bytes;
import static com.github.charlemaznable.core.codec.Bytes.string;
import static com.github.charlemaznable.core.lang.Condition.checkNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SM2 extends GM {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String ALGO_NAME_EC = "EC";

    private static final SM2P256V1Curve CURVE = new SM2P256V1Curve();
    private static final BigInteger SM2_ECC_GX = new BigInteger(
            "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    private static final BigInteger SM2_ECC_GY = new BigInteger(
            "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);
    private static final ECPoint G_POINT = CURVE.createPoint(SM2_ECC_GX, SM2_ECC_GY);
    private static final BigInteger SM2_ECC_N = CURVE.getOrder();
    private static final BigInteger SM2_ECC_H = CURVE.getCofactor();
    private static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(CURVE, G_POINT,
            SM2_ECC_N, SM2_ECC_H);

    /////////// key generate ///////////

    @SneakyThrows
    public static KeyPair generateKeyPair() {
        val kpg = KeyPairGenerator.getInstance(ALGO_NAME_EC, BouncyCastleProvider.PROVIDER_NAME);
        kpg.initialize(new ECParameterSpec(CURVE, G_POINT, SM2_ECC_N, SM2_ECC_H), RANDOM);
        return kpg.generateKeyPair();
    }

    public static BCECPublicKey getPublicKey(KeyPair keyPair) {
        return (BCECPublicKey) keyPair.getPublic();
    }

    public static BCECPrivateKey getPrivateKey(KeyPair keyPair) {
        return (BCECPrivateKey) keyPair.getPrivate();
    }

    public static AsymmetricCipherKeyPair generateKeyPairParameter() {
        val eckpg = new ECKeyPairGenerator();
        eckpg.init(new ECKeyGenerationParameters(DOMAIN_PARAMS, RANDOM));
        return eckpg.generateKeyPair();
    }

    public static ECPublicKeyParameters getPublicKeyParameters(AsymmetricCipherKeyPair keyPair) {
        return (ECPublicKeyParameters) keyPair.getPublic();
    }

    public static ECPrivateKeyParameters getPrivateKeyParameters(AsymmetricCipherKeyPair keyPair) {
        return (ECPrivateKeyParameters) keyPair.getPrivate();
    }

    /////////// pub encrypt ///////////

    public static byte[] encrypt(BCECPublicKey pubKey, String plain) {
        return encrypt(Mode.C1C3C2, parametersFrom(pubKey), plain);
    }

    public static byte[] encrypt(Mode mode, BCECPublicKey pubKey, String plain) {
        return encrypt(mode, parametersFrom(pubKey), plain);
    }

    public static byte[] encrypt(ECPublicKeyParameters pubKeyParameters, String plain) {
        return encrypt(Mode.C1C3C2, pubKeyParameters, plain);
    }

    @SneakyThrows
    public static byte[] encrypt(Mode mode, ECPublicKeyParameters pubKeyParameters, String plain) {
        val engine = new SM2Engine(mode);
        engine.init(true, randomWith(pubKeyParameters));
        val data = bytes(plain);
        return engine.processBlock(data, 0, data.length);
    }

    /////////// pri decrypt ///////////

    public static String decrypt(BCECPrivateKey priKey, byte[] cipher) {
        return decrypt(Mode.C1C3C2, parametersFrom(priKey), cipher);
    }

    public static String decrypt(Mode mode, BCECPrivateKey priKey, byte[] cipher) {
        return decrypt(mode, parametersFrom(priKey), cipher);
    }

    public static String decrypt(ECPrivateKeyParameters priKeyParameters, byte[] cipher) {
        return decrypt(Mode.C1C3C2, priKeyParameters, cipher);
    }

    @SneakyThrows
    public static String decrypt(Mode mode, ECPrivateKeyParameters priKeyParameters, byte[] cipher) {
        val engine = new SM2Engine(mode);
        engine.init(false, priKeyParameters);
        return string(engine.processBlock(cipher, 0, cipher.length));
    }

    /////////// pri sign ///////////

    public static byte[] sign(BCECPrivateKey priKey, String plain) {
        return sign(parametersFrom(priKey), null, plain);
    }

    public static byte[] sign(BCECPrivateKey priKey, String id, String plain) {
        return sign(parametersFrom(priKey), id, plain);
    }

    public static byte[] sign(ECPrivateKeyParameters priKeyParameters, String plain) {
        return sign(priKeyParameters, null, plain);
    }

    @SneakyThrows
    public static byte[] sign(ECPrivateKeyParameters priKeyParameters, String id, String plain) {
        val signer = new SM2Signer();
        signer.init(true, idWith(randomWith(priKeyParameters), id));
        val data = bytes(plain);
        signer.update(data, 0, data.length);
        return signer.generateSignature();
    }

    /////////// pub verify ///////////

    public static boolean verify(BCECPublicKey pubKey, String plain, byte[] sign) {
        return verify(parametersFrom(pubKey), null, plain, sign);
    }

    public static boolean verify(BCECPublicKey pubKey, String id, String plain, byte[] sign) {
        return verify(parametersFrom(pubKey), id, plain, sign);
    }

    public static boolean verify(ECPublicKeyParameters pubKeyParameters, String plain, byte[] sign) {
        return verify(pubKeyParameters, null, plain, sign);
    }

    public static boolean verify(ECPublicKeyParameters pubKeyParameters, String id, String plain, byte[] sign) {
        val signer = new SM2Signer();
        signer.init(false, idWith(pubKeyParameters, id));
        val data = bytes(plain);
        signer.update(data, 0, data.length);
        return signer.verifySignature(sign);
    }

    /////////// private methods ///////////

    private static CipherParameters randomWith(CipherParameters parameters) {
        return new ParametersWithRandom(parameters, RANDOM);
    }

    private static CipherParameters idWith(CipherParameters parameters, String id) {
        return checkNull(bytes(id), () -> parameters, b -> new ParametersWithID(parameters, b));
    }

    private static ECPublicKeyParameters parametersFrom(BCECPublicKey ecPubKey) {
        val parameterSpec = ecPubKey.getParameters();
        val domainParameters = new ECDomainParameters(parameterSpec.getCurve(),
                parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());
        return new ECPublicKeyParameters(ecPubKey.getQ(), domainParameters);
    }

    private static ECPrivateKeyParameters parametersFrom(BCECPrivateKey ecPriKey) {
        val parameterSpec = ecPriKey.getParameters();
        val domainParameters = new ECDomainParameters(parameterSpec.getCurve(),
                parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());
        return new ECPrivateKeyParameters(ecPriKey.getD(), domainParameters);
    }
}
