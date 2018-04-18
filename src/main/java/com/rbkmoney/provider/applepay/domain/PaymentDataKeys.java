package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;

/**
 * Created by vpankrashkin on 17.04.18.
 */
public class PaymentDataKeys {
    @JsonProperty(value = "applicationPrimaryAccountNumber", required = true)
    private String appPrimaryAccountNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyMMdd")
    @JsonProperty(value = "applicationExpirationDate", required = true)
    private LocalDate appExpirationDate;

    @JsonProperty(value = "currencyCode", required = true)
    private String currencyCode;

    @JsonProperty(value = "transactionAmount", required = true)
    private long transactionAmount;

    @JsonProperty(value = "cardholderName")
    private String cardholderName;

    @JsonProperty(value = "deviceManufacturerIdentifier", required = true)
    private String devManufacturerIdentifier;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "paymentDataType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Auth3DS.class, name = "3DSecure"),
            @JsonSubTypes.Type(value = AuthEMV.class, name = "EMV")
    })
    private AuthData authData;

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

    public AuthData getAuthData() {
        return authData;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    @Override
    public String toString() {
        return "PaymentDataKeys{" +
                "appPrimaryAccountNumber='" + appPrimaryAccountNumber + '\'' +
                ", appExpirationDate=" + appExpirationDate +
                ", currencyCode='" + currencyCode + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", cardholderName='" + cardholderName + '\'' +
                ", devManufacturerIdentifier='" + devManufacturerIdentifier + '\'' +
                ", authData=" + authData +
                '}';
    }
}
