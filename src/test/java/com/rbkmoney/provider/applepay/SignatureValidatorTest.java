package com.rbkmoney.provider.applepay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.provider.applepay.domain.PaymentData;
import com.rbkmoney.provider.applepay.service.SignatureValidator;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by vpankrashkin on 30.03.18.
 */
public class SignatureValidatorTest {

    @Test
    public void testValidation() throws Exception {
        PaymentData paymentData = createPaymentData("rbk_data1.json");

        new SignatureValidator(
                IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("AppleRootCA-G3.cer")),
                -1)
                .validate(paymentData.getHeader(), paymentData.getData(), paymentData.getSignature(), paymentData.getVersion());
    }


    private static PaymentData createPaymentData(String name) throws IOException {
        return new ObjectMapper().readValue(SignatureValidatorTest.class.getClassLoader().getResourceAsStream(name), PaymentData.class);
    }
}
