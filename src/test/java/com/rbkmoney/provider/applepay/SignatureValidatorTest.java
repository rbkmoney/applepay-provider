package com.rbkmoney.provider.applepay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.provider.applepay.domain.PaymentData;
import com.rbkmoney.provider.applepay.service.SignatureValidator;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by vpankrashkin on 30.03.18.
 */
public class SignatureValidatorTest {

    @Test
    public void test1() throws Exception {
        PaymentData paymentData = createPaymentData("rbk_data1.json");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            new SignatureValidator(-1).validate(paymentData.getHeader(), paymentData.getData(), paymentData.getSignature(), paymentData.getVersion());
        }
        System.out.println((System.currentTimeMillis() - start)/10);
    }


    private static PaymentData createPaymentData(String name) throws IOException {
        return new ObjectMapper().readValue(SignatureValidatorTest.class.getClassLoader().getResourceAsStream(name), PaymentData.class);
    }
}
