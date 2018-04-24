package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 29.03.18.
 */
public class PaymentMethod {
    @JsonProperty(value = "displayName")
    private String displayName;

    @JsonProperty(value = "network")
    private String paymentNetwork;

    @JsonProperty(value = "type", required = true)
    private String paymentMethodType;

    @JsonProperty(value = "paymentPass", required = true)
    private String paymentPass;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPaymentNetwork() {
        return paymentNetwork;
    }

    public void setPaymentNetwork(String paymentNetwork) {
        this.paymentNetwork = paymentNetwork;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public String getPaymentPass() {
        return paymentPass;
    }

    public void setPaymentPass(String paymentPass) {
        this.paymentPass = paymentPass;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "displayName='" + displayName + '\'' +
                ", paymentNetwork='" + paymentNetwork + '\'' +
                ", paymentMethodType='" + paymentMethodType + '\'' +
                ", paymentPass='" + paymentPass + '\'' +
                '}';
    }
}
