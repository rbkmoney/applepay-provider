package com.rbkmoney.provider.applepay.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentHeader {

    @JsonProperty(value = "applicationData")
    private String applicationData;

    @JsonProperty(value = "ephemeralPublicKey")
    private String ephemeralPublicKey;

    @JsonProperty(value = "wrapppedKey")
    private String wrappedKey;

    @JsonProperty(value = "publicKeyHash", required = true)
    private String publicKeyHash;

    @JsonProperty(value = "transactionId", required = true)
    private String transactionId;

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
