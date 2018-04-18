package com.rbkmoney.provider.applepay.service;

import com.rbkmoney.provider.applepay.domain.PaymentToken;
import com.rbkmoney.provider.applepay.store.APCertStore;

import java.util.Base64;

/**
 * Created by vpankrashkin on 16.04.18.
 */
public class DecryptionService {
    private final APCertStore certStore;
    private final char[] pksc12KeyPass;

    public DecryptionService(APCertStore certStore, char[] pksc12KeyPass) {
        this.certStore = certStore;
        this.pksc12KeyPass = pksc12KeyPass;
    }

    public String decryptToken(String merchantId, PaymentToken paymentToken) throws CryptoException, CertNotFoundException {
        String keyHash = paymentToken.getPaymentData().getHeader().getPublicKeyHash().substring(0, 8);
        byte[] merchCertData = certStore.getProcessingCert(merchantId, keyHash);
        byte[] pkcs12KeyData = certStore.getProcessingKeyCert(merchantId, merchantId);

        if (merchCertData == null || pkcs12KeyData == null) {
            throw new CertNotFoundException("One or more keys're not found for merchant: "+merchantId);
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
}
