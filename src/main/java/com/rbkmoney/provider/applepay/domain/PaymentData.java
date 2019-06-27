package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
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

}
