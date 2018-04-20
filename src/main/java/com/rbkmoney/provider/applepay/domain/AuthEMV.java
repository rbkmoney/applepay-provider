package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 17.04.18.
 */
public class AuthEMV extends AuthData {
    @JsonProperty(value = "emvData", required = true)
    private String emvData;

    @JsonProperty(value = "encryptedPINData", required = true)
    private String encryptedPINData;

    public String getEmvData() {
        return emvData;
    }

    public void setEmvData(String emvData) {
        this.emvData = emvData;
    }

    public String getEncryptedPINData() {
        return encryptedPINData;
    }

    public void setEncryptedPINData(String encryptedPINData) {
        this.encryptedPINData = encryptedPINData;
    }

    @Override
    public String toString() {
        return "AuthEMV{" +
                "emvData='" + emvData + '\'' +
                ", encryptedPINData='" + encryptedPINData + '\'' +
                '}';
    }
}
