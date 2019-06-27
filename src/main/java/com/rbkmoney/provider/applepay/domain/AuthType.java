package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AuthType {
    @JsonProperty("3DSecure")
    Auth3DS,
    @JsonProperty("EMV")
    AuthEMV
}
