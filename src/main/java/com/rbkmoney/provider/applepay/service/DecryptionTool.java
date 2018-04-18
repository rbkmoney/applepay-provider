package com.rbkmoney.provider.applepay.service;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Enumeration;

public class DecryptionTool {
    // Apple Pay uses an 0s for the IV
    private static final byte[] IV = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    // Precompute prefix bytes for the 'other' parameter of the NIST contact KDF
    private static final byte[] KDF_OTHER_BYTES_PREFIX;

    private static final String BC = "BC";


    static {
        if (Security.getProvider(BC) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        try {
            KDF_OTHER_BYTES_PREFIX = ((char) 0x0D + "id-aes256-GCM" + "Apple").getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public static String decrypt(String ephemeralPublicKeyData, byte[] tokenData,  byte[] merchantCerData, byte[] pkcs12KeyData, char[] pkcs12KeyPass) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        // read the ephemeral public key. It's a PEM file without header/footer -- add it back to make our lives easy
        String ephemeralPubKeyStr = "-----BEGIN PUBLIC KEY-----\n" + ephemeralPublicKeyData + "\n-----END PUBLIC KEY-----";
        PEMReader pemReaderPublic = new PEMReader(new StringReader(ephemeralPubKeyStr));
        ECPublicKey ephemeralPublicKey = (ECPublicKey) pemReaderPublic.readObject();

        // Apple assigns a merchant identifier and places it in an extension (OID 1.2.840.113635.100.6.32)
        final X509Certificate merchantCertificate = readDerEncodedX509Certificate(new ByteArrayInputStream(merchantCerData));
        byte[] merchantIdentifier = extractMerchantIdentifier(merchantCertificate);

        // load the merchant EC private key
        ECPrivateKey merchantPrivateKey = loadPrivateKey(pkcs12KeyData, pkcs12KeyPass);

        // decrypt per Apple Pay spec
        final byte[] plaintext = decrypt(tokenData, merchantPrivateKey, ephemeralPublicKey, merchantIdentifier);
        return new String(plaintext, "ASCII");
    }


    private static byte[] decrypt(byte[] cipherText, ECPrivateKey merchantPrivateKey, ECPublicKey ephemeralPublicKey, byte[] merchantIdentifier) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        // ECDH key agreement
        final KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "BC");
        keyAgreement.init(merchantPrivateKey);
        keyAgreement.doPhase(ephemeralPublicKey, true);
        final byte[] sharedSecret = keyAgreement.generateSecret();

        // NIST key derivation function w/ Apple Pay specific parameters
        byte[] partyV = merchantIdentifier;
        byte[] other = new byte[KDF_OTHER_BYTES_PREFIX.length + partyV.length];
        System.arraycopy(KDF_OTHER_BYTES_PREFIX, 0, other, 0, KDF_OTHER_BYTES_PREFIX.length);
        System.arraycopy(partyV, 0, other, KDF_OTHER_BYTES_PREFIX.length, partyV.length);

        final Digest digest = new SHA256Digest();
        KDFConcatGenerator kdfConcatGenerator = new KDFConcatGenerator(digest, other);
        kdfConcatGenerator.init(new KDFParameters(sharedSecret, null));
        byte[] aesKey = new byte[32];
        kdfConcatGenerator.generateBytes(aesKey, 0, aesKey.length);

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        final SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(IV));
        return cipher.doFinal(cipherText);
    }

    private static ECPrivateKey loadPrivateKey(byte[] pkcs12Data, char[] pass) throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        KeyStore keystore = KeyStore.getInstance("PKCS12", "BC");
        keystore.load(new ByteArrayInputStream(pkcs12Data), pass);
        assert keystore.size() == 1 : "wrong number of entries in keychain";
        Enumeration<String> aliases = keystore.aliases();
        String alias = null;
        while (aliases.hasMoreElements()) {
            alias = aliases.nextElement();
        }
        return (ECPrivateKey) keystore.getKey(alias, null);
    }

    private static byte[] extractMerchantIdentifier(X509Certificate merchantCertificate) throws UnsupportedEncodingException {
        byte[] merchantIdentifierTlv = merchantCertificate.getExtensionValue("1.2.840.113635.100.6.32");
        assert merchantIdentifierTlv.length == 68;
        byte[] merchantIdentifier = new byte[64];
        System.arraycopy(merchantIdentifierTlv, 4, merchantIdentifier, 0, 64);
        return hexStringToByteArray(new String(merchantIdentifier, "ASCII"));
    }

    private static X509Certificate readDerEncodedX509Certificate(InputStream in) throws FileNotFoundException, CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(in);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
