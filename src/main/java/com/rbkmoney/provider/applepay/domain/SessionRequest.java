package com.rbkmoney.provider.applepay.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by vpankrashkin on 04.05.18.
 */
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

    public String getMerchantId() {
        return merchantId;
    }

    public String getValidationURL() {
        return validationURL;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "SessionRequest{" +
                "merchantId='" + merchantId + '\'' +
                ", validationURL='" + validationURL + '\'' +
                ", body=" + body +
                '}';
    }
}
