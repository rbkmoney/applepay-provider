package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 17.04.18.
 */
public class AuthEMV extends AuthData {
    private String emvData;
    private String encryptedPINData;

    @JsonCreator
    public AuthEMV(
            @JsonProperty(value = "emvData", required = true) String emvData,
            @JsonProperty(value = "encryptedPINData", required = true) String encryptedPINData) {
        this.emvData = emvData;
        this.encryptedPINData = encryptedPINData;
    }

    public AuthEMV() {
    }

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
