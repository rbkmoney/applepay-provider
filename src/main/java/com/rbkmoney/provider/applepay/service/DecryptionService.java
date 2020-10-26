package com.rbkmoney.provider.applepay.service;

import com.rbkmoney.provider.applepay.domain.PaymentToken;
import com.rbkmoney.provider.applepay.store.APCertStore;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static com.rbkmoney.provider.applepay.service.DecryptionTool.*;

@RequiredArgsConstructor
public class DecryptionService {

    private final APCertStore certStore;
    private final char[] pksc12KeyPass;

    public String decryptToken(String merchantId, PaymentToken paymentToken) throws CryptoException {
        String keyHash = Hex.encodeHexString(
                Base64.getDecoder().decode(paymentToken.getPaymentData().getHeader().getPublicKeyHash())
        );
        String keyHashSubstring = keyHash.substring(0, 7);

        File[] merchantCertsList = certStore.getMerchantCertsList();
        try {
            for (File fileName : merchantCertsList) {
                byte[] pkcs12Data = Files.readAllBytes(fileName.toPath());
                X509Certificate merchantCertificate = getCertificate(pkcs12Data, pksc12KeyPass, fileName);
                if (merchantCertificate != null) {
                    if (certificateMatchesKeyAndMerchantId(merchantCertificate, keyHash, merchantId)) {
                        return DecryptionTool.decrypt(paymentToken.getPaymentData().getHeader().getEphemeralPublicKey(),
                                Base64.getDecoder().decode(paymentToken.getPaymentData().getData()),
                                pkcs12Data,
                                merchantCertificate,
                                pksc12KeyPass
                        );
                    }
                }
            }
        } catch (Exception e) {
            throw new CryptoException(e);
        }

        byte[] merchCertData = certStore.getProcessingCert(merchantId, keyHashSubstring);
        byte[] pkcs12KeyData = certStore.getProcessingKeyCert(merchantId, keyHashSubstring);

        if (merchCertData == null || pkcs12KeyData == null) {
            throw new CertNotFoundException("One or more keys're not found for merchant: " + merchantId);
        }
        try {
            return DecryptionTool.decrypt(paymentToken.getPaymentData().getHeader().getEphemeralPublicKey(),
                    Base64.getDecoder().decode(paymentToken.getPaymentData().getData()),
                    merchCertData,
                    pkcs12KeyData,
                    pksc12KeyPass
            );

        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public boolean certificateMatchesKeyAndMerchantId(X509Certificate merchantCertificate, String keyHash, String merchantId) throws NoSuchAlgorithmException {
        return (pubKeyHash(merchantCertificate).equals(keyHash) &&
                merchantId.equals(getMerchantUID(merchantCertificate)));
    }
}
