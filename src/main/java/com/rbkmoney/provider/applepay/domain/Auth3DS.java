package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 17.04.18.
 */
public class Auth3DS extends AuthData {
    @JsonProperty(value = "onlinePaymentCryptogram", required = true)
    private String cryptogram;

    @JsonProperty(value = "eciIndicator")
    private String eci;

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
