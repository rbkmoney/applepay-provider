package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 29.03.18.
 */
public class PaymentMethod {
    private String displayName;
    private String paymentNetwork;
    private String paymentMethodType;
    private String paymentPass;

    @JsonCreator
    public PaymentMethod(
            @JsonProperty(value = "displayName") String displayName,
            @JsonProperty(value = "network") String paymentNetwork,
            @JsonProperty(value = "type", required = true) String paymentMethodType,
            @JsonProperty(value = "paymentPass") String paymentPass) {
        this.displayName = displayName;
        this.paymentNetwork = paymentNetwork;
        this.paymentMethodType = paymentMethodType;
        this.paymentPass = paymentPass;
    }

    public PaymentMethod() {
    }

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
