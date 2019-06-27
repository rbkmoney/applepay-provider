package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaymentDataKeys {

    @ToString.Exclude
    private String appPrimaryAccountNumber;
    @ToString.Exclude
    private LocalDate appExpirationDate;
    private String currencyCode;
    private long transactionAmount;
    private String cardholderName;
    private String devManufacturerIdentifier;
    private AuthType authType;
    private AuthData authData;

    @JsonCreator
    public PaymentDataKeys(
            @JsonProperty(value = "applicationPrimaryAccountNumber", required = true) String appPrimaryAccountNumber,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyMMdd")
            @JsonProperty(value = "applicationExpirationDate", required = true) LocalDate appExpirationDate,
            @JsonProperty(value = "currencyCode", required = true) String currencyCode,
            @JsonProperty(value = "transactionAmount", required = true) long transactionAmount,
            @JsonProperty(value = "cardholderName") String cardholderName,
            @JsonProperty(value = "deviceManufacturerIdentifier", required = true) String devManufacturerIdentifier,
            @JsonProperty("paymentDataType") AuthType authType,
            @JsonProperty(value = "paymentData", required = true) AuthData authData) {
        this.appPrimaryAccountNumber = appPrimaryAccountNumber;
        this.appExpirationDate = appExpirationDate;
        this.currencyCode = currencyCode;
        this.transactionAmount = transactionAmount;
        this.cardholderName = cardholderName;
        this.devManufacturerIdentifier = devManufacturerIdentifier;
        this.authType = authType;
        this.authData = authData;
    }

}
