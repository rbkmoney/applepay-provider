package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@NoArgsConstructor
public class SessionRequest {
    private String merchantId;
    private String validationURL;
    private Map<String, Object> body;

    @JsonCreator
    public SessionRequest(@JsonProperty(value = "merchantId", required = true) String merchantId,
                          @JsonProperty(value = "validationURL", required = true) String validationURL,
                          @JsonProperty(value = "body", required = true) Map<String, Object> body) {
        this.merchantId = merchantId;
        this.validationURL = validationURL;
        this.body = body;
    }

}
