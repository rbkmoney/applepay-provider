package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vpankrashkin on 20.04.18.
 */
public enum AuthType {
    @JsonProperty("3DSecure")
    Auth3DS,
    @JsonProperty("EMV")
    AuthEMV
}
