package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDate;

/**
 * Created by vpankrashkin on 17.04.18.
 */
public class PaymentDataKeys {
    private String appPrimaryAccountNumber;
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

    public PaymentDataKeys() {
    }

    public String getAppPrimaryAccountNumber() {
        return appPrimaryAccountNumber;
    }

    public void setAppPrimaryAccountNumber(String appPrimaryAccountNumber) {
        this.appPrimaryAccountNumber = appPrimaryAccountNumber;
    }

    public LocalDate getAppExpirationDate() {
        return appExpirationDate;
    }

    public void setAppExpirationDate(LocalDate appExpirationDate) {
        this.appExpirationDate = appExpirationDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public long getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getDevManufacturerIdentifier() {
        return devManufacturerIdentifier;
    }

    public void setDevManufacturerIdentifier(String devManufacturerIdentifier) {
        this.devManufacturerIdentifier = devManufacturerIdentifier;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    @Override
    public String toString() {
        return "PaymentDataKeys{" +
                "appPrimaryAccountNumber='" + (appPrimaryAccountNumber != null ? "***" : null) + '\'' +
                ", appExpirationDate=" + (appExpirationDate != null ? "***" : null) +
                ", currencyCode='" + currencyCode + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", cardholderName='" + cardholderName + '\'' +
                ", devManufacturerIdentifier='" + devManufacturerIdentifier + '\'' +
                ", authType=" + authType +
                ", authData=" + authData +
                '}';
    }
}
