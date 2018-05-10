package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Created by vpankrashkin on 29.03.18.
 */
@JsonRootName("token")
public class PaymentToken {
    private PaymentData paymentData;
    private PaymentMethod paymentMethod;
    private String transactionId;

    @JsonCreator
    public PaymentToken(
            @JsonProperty(value = "paymentData", required = true) PaymentData paymentData,
            @JsonProperty(value = "paymentMethod", required = true) PaymentMethod paymentMethod,
            @JsonProperty(value = "transactionIdentifier", required = true) String transactionId) {
        this.paymentData = paymentData;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
    }

    public PaymentToken() {
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(PaymentData paymentData) {
        this.paymentData = paymentData;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "PaymentToken{" +
                "paymentData=" + paymentData +
                ", paymentMethod=" + paymentMethod +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}
