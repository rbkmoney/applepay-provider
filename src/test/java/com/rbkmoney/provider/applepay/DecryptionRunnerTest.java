package com.rbkmoney.provider.applepay;

import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.damsel.payment_tool_provider.*;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

/**
 * Created by vpankrashkin on 19.04.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "cert.identity.path=./target/test-classes/apple_keys/identity",
                "cert.processing.path=./target/test-classes/apple_keys/processing",
                "apple.expiration_time=-1"
        }
)

@Ignore
public class DecryptionRunnerTest {

    @Value("http://127.0.0.1:${server.port}/provider/apple")
    private String thriftUrl;

    PaymentToolProviderSrv.Iface client;


    @Before
    public void setUp() throws URISyntaxException {
        client = new THSpawnClientBuilder()
                .withNetworkTimeout(5000)
                .withAddress(new URI(thriftUrl))
                .build(PaymentToolProviderSrv.Iface.class);
    }

    @Test
    public void testDecryption() throws IOException, TException {
        byte[] paymentToken = IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("tinkoff.json"));

        ApplePayRequest payRequest = new ApplePayRequest("merchant.money.rbk.checkout", new Content("application/json", ByteBuffer.wrap(paymentToken
        )));
        WrappedPaymentTool wrapped = new WrappedPaymentTool(PaymentRequest.apple(payRequest));
        UnwrappedPaymentTool unwrapped = client.unwrap(wrapped);
    }

    @Test(expected = InvalidRequest.class)
    public void testMissingFields() throws TException {
        byte[] paymentToken = "{\"token\":{\"paymentMethod\":{\"displayName\":\"Simulated Instrument\",\"network\":\"MasterCard\"},\"transactionIdentifier\":\"Simulated Identifier\"}}".getBytes();
        ApplePayRequest payRequest = new ApplePayRequest("merchant.money.rbk.checkout", new Content("application/json", ByteBuffer.wrap(paymentToken
        )));
        WrappedPaymentTool wrapped = new WrappedPaymentTool(PaymentRequest.apple(payRequest));
        UnwrappedPaymentTool unwrapped = client.unwrap(wrapped);
    }
}
