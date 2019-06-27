package com.rbkmoney.provider.applepay.store;

public class CertStoreException extends RuntimeException {
    public CertStoreException() {
    }

    public CertStoreException(String message) {
        super(message);
    }

    public CertStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertStoreException(Throwable cause) {
        super(cause);
    }

    public CertStoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
