package com.rbkmoney.provider.applepay;

import com.rbkmoney.damsel.payment_tool_provider.PaymentToolProviderSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * Created by vpankrashkin on 12.04.18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "cert.identity.path=./target/test-classes/apple_keys",
                "cert.processing.path=/./target/test-classes/apple_keys"
        }
)
public class AppRunner {

    @Value("http://127.0.0.1:${server.port}/provider/apple")
    private String thriftUrl;

    @Value("http://127.0.0.1:${server.http_port}/${server.http_path_prefix}/session")
    private String sessionUrl;


    PaymentToolProviderSrv.Iface client;
    RestTemplate restTemplate;

    @Before
    public void setUp() throws URISyntaxException {
        client = new THSpawnClientBuilder()
                .withNetworkTimeout(0)
                .withAddress(new URI(thriftUrl))
                .build(PaymentToolProviderSrv.Iface.class);
        restTemplate = new RestTemplate();
    }

    @Test
    public void test() throws InterruptedException, URISyntaxException {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(new HashMap() {{
            put("merchantId", "merchant.money.rbk.checkout");
            put("domainName", "applefags.rbkmoney.com");
            put("displayName", "RBKmoney Checkout");
            put("validationURL", "https://apple-pay-gateway.apple.com/paymentservices/startSession");
        }});
        ResponseEntity<String> response = restTemplate.exchange(sessionUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println(response.getBody());
    }
}
