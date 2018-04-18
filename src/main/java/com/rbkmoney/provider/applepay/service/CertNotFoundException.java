package com.rbkmoney.provider.applepay.service;

/**
 * Created by vpankrashkin on 10.04.18.
 */
public class CertNotFoundException extends CryptoException {
    public CertNotFoundException() {
    }

    public CertNotFoundException(String message) {
        super(message);
    }

    public CertNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertNotFoundException(Throwable cause) {
        super(cause);
    }

    public CertNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
