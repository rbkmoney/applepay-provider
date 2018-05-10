package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 29.03.18.
 */
public class PaymentData {
    private String version;
    private String data;
    private String signature;
    private PaymentHeader header;

    @JsonCreator
    public PaymentData(
            @JsonProperty(value = "version", required = true) String version,
            @JsonProperty(value = "data", required = true) String data,
            @JsonProperty(value = "signature", required = true) String signature,
            @JsonProperty(value = "header", required = true) PaymentHeader header) {
        this.version = version;
        this.data = data;
        this.signature = signature;
        this.header = header;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public PaymentHeader getHeader() {
        return header;
    }

    public void setHeader(PaymentHeader header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "PaymentData{" +
                "version='" + version + '\'' +
                ", data='" + data + '\'' +
                ", signature='" + signature + '\'' +
                ", header=" + header +
                '}';
    }
}
