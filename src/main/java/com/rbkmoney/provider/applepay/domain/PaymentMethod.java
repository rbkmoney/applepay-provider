package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
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

}
