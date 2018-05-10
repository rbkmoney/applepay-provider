package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 17.04.18.
 */
public class Auth3DS extends AuthData {
    private String cryptogram;
    private String eci;

    @JsonCreator
    public Auth3DS(
            @JsonProperty(value = "onlinePaymentCryptogram", required = true) String cryptogram,
            @JsonProperty(value = "eciIndicator") String eci) {
        this.cryptogram = cryptogram;
        this.eci = eci;
    }

    public Auth3DS() {
    }

    public String getCryptogram() {
        return cryptogram;
    }

    public void setCryptogram(String cryptogram) {
        this.cryptogram = cryptogram;
    }

    public String getEci() {
        return eci;
    }

    public void setEci(String eci) {
        this.eci = eci;
    }

    @Override
    public String toString() {
        return "Auth3DS{" +
                "cryptogram='" + cryptogram + '\'' +
                ", eci='" + eci + '\'' +
                "} " + super.toString();
    }
}
