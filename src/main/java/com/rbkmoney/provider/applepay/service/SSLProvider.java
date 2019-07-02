package com.rbkmoney.provider.applepay.service;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class SSLProvider {

    public SSLSocketFactory getSSLForClient(byte[] identityCert, char[] pass) throws CryptoException {
        try {
            KeyStore appKeyStore = KeyStore.getInstance("PKCS12");
            appKeyStore.load(new ByteArrayInputStream(identityCert), pass);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(appKeyStore, pass);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            SSLContext context = SSLContext.getInstance("TLSv1.2");
            context.init(keyManagers, getTrustManagerFactory().getTrustManagers(), null);

            return context.getSocketFactory();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) {
            throw new CryptoException(ex);
        } catch (IOException ex) {
            throw new RuntimeException("Unexpected error", ex);
        }
    }

    public X509TrustManager getX509TrustManager() throws CryptoException {
        TrustManager[] trustManagers = getTrustManagerFactory().getTrustManagers();
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new CryptoException("Unexpected default trust managers: " + Arrays.toString(trustManagers));
    }

    public TrustManagerFactory getTrustManagerFactory() throws CryptoException {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            return tmf;
        } catch (NoSuchAlgorithmException | KeyStoreException ex) {
            throw new CryptoException(ex);
        }
    }

}


