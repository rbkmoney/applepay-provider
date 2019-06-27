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
public class AuthEMV extends AuthData {

    private String emvData;
    private String encryptedPINData;

    @JsonCreator
    public AuthEMV(
            @JsonProperty(value = "emvData", required = true) String emvData,
            @JsonProperty(value = "encryptedPINData", required = true) String encryptedPINData) {
        this.emvData = emvData;
        this.encryptedPINData = encryptedPINData;
    }

}
