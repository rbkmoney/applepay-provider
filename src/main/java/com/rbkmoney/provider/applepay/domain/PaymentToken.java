package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 29.03.18.
 */
public class PaymentToken {
    @JsonProperty(value = "paymentData", required = true)
    private PaymentData paymentData;

    @JsonProperty(value = "paymentMethod", required = true)
    private PaymentMethod paymentMethod;

    @JsonProperty(value = "transactionIdentifier", required = true)
    private String transactionId;

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
