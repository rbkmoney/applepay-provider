package com.rbkmoney.provider.applepay.service;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by vpankrashkin on 12.04.18.
 */
public class SSLProvider {

    public SSLSocketFactory getSSLForClient(byte[] identityCert, char[] pass) throws CryptoException {
        try {
            KeyStore appKeyStore = KeyStore.getInstance("PKCS12");
            appKeyStore.load(new ByteArrayInputStream(identityCert), pass);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(appKeyStore, pass);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            TrustManagerFactory tmf =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagers, trustManagers, null);

            return context.getSocketFactory();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
            throw new CryptoException(e);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error", e);
        }
    }

}


