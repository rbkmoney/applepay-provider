package com.rbkmoney.provider.applepay.domain;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentHeader {
    private String applicationData;
    private String ephemeralPublicKey;
    private String wrappedKey;
    private String publicKeyHash;
    private String transactionId;

    @JsonCreator
    public PaymentHeader(
            @JsonProperty(value = "applicationData") String applicationData,
            @JsonProperty(value = "ephemeralPublicKey") String ephemeralPublicKey, //EC_v1 only
            @JsonProperty(value = "wrapppedKey") String wrappedKey, //RSA_v1 only
            @JsonProperty(value = "publicKeyHash", required = true) String publicKeyHash,
            @JsonProperty(value = "transactionId", required = true) String transactionId) {
        this.applicationData = applicationData;
        this.ephemeralPublicKey = ephemeralPublicKey;
        this.wrappedKey = wrappedKey;
        this.publicKeyHash = publicKeyHash;
        this.transactionId = transactionId;
    }

    public PaymentHeader() {
    }

    public String getApplicationData() {
        return applicationData;
    }

    public void setApplicationData(String applicationData) {
        this.applicationData = applicationData;
    }

    public String getEphemeralPublicKey() {
        return ephemeralPublicKey;
    }

    public void setEphemeralPublicKey(String ephemeralPublicKey) {
        this.ephemeralPublicKey = ephemeralPublicKey;
    }

    public String getWrappedKey() {
        return wrappedKey;
    }

    public void setWrappedKey(String wrappedKey) {
        this.wrappedKey = wrappedKey;
    }

    public String getPublicKeyHash() {
        return publicKeyHash;
    }

    public void setPublicKeyHash(String publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "PaymentHeader{" +
                "applicationData='" + applicationData + '\'' +
                ", ephemeralPublicKey='" + ephemeralPublicKey + '\'' +
                ", wrappedKey='" + wrappedKey + '\'' +
                ", publicKeyHash='" + publicKeyHash + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
