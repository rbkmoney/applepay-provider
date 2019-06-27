package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
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

}
