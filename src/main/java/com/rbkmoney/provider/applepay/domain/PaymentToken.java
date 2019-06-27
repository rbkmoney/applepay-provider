package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
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

}
