package com.rbkmoney.provider.applepay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rbkmoney.provider.applepay.domain.Auth3DS;
import com.rbkmoney.provider.applepay.domain.PaymentData;
import com.rbkmoney.provider.applepay.domain.PaymentDataKeys;
import com.rbkmoney.provider.applepay.service.SignatureValidator;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Created by vpankrashkin on 30.03.18.
 */
public class SignatureValidatorTest {

    @Test
    public void testValidation() throws Exception {
        PaymentData paymentData = createPaymentData("tinkoff_token.json");

        new SignatureValidator(
                IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("AppleRootCA-G3.cer")),
                -1)
                .validate(paymentData.getHeader(), paymentData.getData(), paymentData.getSignature(), paymentData.getVersion());
    }


    private static PaymentData createPaymentData(String name) throws IOException {
        return new ObjectMapper().readValue(SignatureValidatorTest.class.getClassLoader().getResourceAsStream(name), PaymentData.class);
    }


    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        PaymentDataKeys dataKeys = new PaymentDataKeys();
        dataKeys.setAppExpirationDate(LocalDate.now());
        dataKeys.setAppPrimaryAccountNumber("prnum");
        dataKeys.setCurrencyCode("123");
        dataKeys.setDevManufacturerIdentifier("dev");
        dataKeys.setTransactionAmount(11111);
        Auth3DS auth3DS = new Auth3DS();
        auth3DS.setCryptogram("c");
        auth3DS.setEci("7");
        dataKeys.setAuthData(auth3DS);
        System.out.println(new ObjectMapper().writerFor(PaymentDataKeys.class).writeValueAsString(dataKeys));
    }
}
