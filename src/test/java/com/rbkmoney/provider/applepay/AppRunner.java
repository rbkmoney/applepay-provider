package com.rbkmoney.provider.applepay;

import com.rbkmoney.damsel.payment_tool_provider.PaymentToolProviderSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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

    @Value("http://127.0.0.1:${server.http_port}/${server.http_path_prefix}/jssession")
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
        try {
            /*HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(new HashMap() {{
                put("merchantId", "merchant.money.rbk.checkout");
                put("domainName", "applefags.rbkmoney.com");
                put("displayName", "RBKmoney Checkout");
                put("validationURL", "https://apple-pay-gateway.apple.com/paymentservices/startSession");
            }}, headers);
            ResponseEntity<String> response = restTemplate.exchange(sessionUrl, HttpMethod.POST, request, String.class);

*/
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("merchantId", "merchant.money.rbk.checkout");
            map.add("validationURL", "https://apple-pay-gateway.apple.com/paymentservices/startSession");
            map.add("body", "{\n" +
                    "    \"epochTimestamp\": 1524066498584,\n" +
                    "    \"expiresAt\": 1524073698584,\n" +
                    "    \"merchantSessionIdentifier\": \"SSH464D67B8BD234400A9AABACDFA09CBD3_2101F68F6980DFE07DEFE987B1CAF2961766C119C8FDCBB33566B1A97F33C9C3\",\n" +
                    "    \"nonce\": \"ab64776d\",\n" +
                    "    \"merchantIdentifier\": \"DB868D999E84203939EA0C331AC8B71B9981290AE1F3CC06471CF282BC1745AD\",\n" +
                    "    \"domainName\": \"applefags.rbkmoney.com\",\n" +
                    "    \"displayName\": \"RBKmoney Checkout\",\n" +
                    "    \"signature\": \"308006092a864886f70d010702a0803080020101310f300d06096086480165030402010500308006092a864886f70d0107010000a080308203e230820388a00302010202082443f2a8069df577300a06082a8648ce3d040302307a312e302c06035504030c254170706c65204170706c69636174696f6e20496e746567726174696f6e204341202d20473331263024060355040b0c1d4170706c652043657274696669636174696f6e20417574686f7269747931133011060355040a0c0a4170706c6520496e632e310b3009060355040613025553301e170d3134303932353232303631315a170d3139303932343232303631315a305f3125302306035504030c1c6563632d736d702d62726f6b65722d7369676e5f5543342d50524f4431143012060355040b0c0b694f532053797374656d7331133011060355040a0c0a4170706c6520496e632e310b30090603550406130255533059301306072a8648ce3d020106082a8648ce3d03010703420004c21577edebd6c7b2218f68dd7090a1218dc7b0bd6f2c283d846095d94af4a5411b83420ed811f3407e83331f1c54c3f7eb3220d6bad5d4eff49289893e7c0f13a38202113082020d304506082b0601050507010104393037303506082b060105050730018629687474703a2f2f6f6373702e6170706c652e636f6d2f6f63737030342d6170706c6561696361333031301d0603551d0e041604149457db6fd57481868989762f7e578507e79b5824300c0603551d130101ff04023000301f0603551d2304183016801423f249c44f93e4ef27e6c4f6286c3fa2bbfd2e4b3082011d0603551d2004820114308201103082010c06092a864886f7636405013081fe3081c306082b060105050702023081b60c81b352656c69616e6365206f6e207468697320636572746966696361746520627920616e7920706172747920617373756d657320616363657074616e6365206f6620746865207468656e206170706c696361626c65207374616e64617264207465726d7320616e6420636f6e646974696f6e73206f66207573652c20636572746966696361746520706f6c69637920616e642063657274696669636174696f6e2070726163746963652073746174656d656e74732e303606082b06010505070201162a687474703a2f2f7777772e6170706c652e636f6d2f6365727469666963617465617574686f726974792f30340603551d1f042d302b3029a027a0258623687474703a2f2f63726c2e6170706c652e636f6d2f6170706c6561696361332e63726c300e0603551d0f0101ff040403020780300f06092a864886f76364061d04020500300a06082a8648ce3d04030203480030450220728a9f0f92a32ab999742bd55eb67340572a9687a1d62ef5359710f5163e96e902210091379c7d6ebe5b9974af40037f34c23ead98b5b4b7f70d355c86b2a81372f1b1308202ee30820275a0030201020208496d2fbf3a98da97300a06082a8648ce3d0403023067311b301906035504030c124170706c6520526f6f74204341202d20473331263024060355040b0c1d4170706c652043657274696669636174696f6e20417574686f7269747931133011060355040a0c0a4170706c6520496e632e310b3009060355040613025553301e170d3134303530363233343633305a170d3239303530363233343633305a307a312e302c06035504030c254170706c65204170706c69636174696f6e20496e746567726174696f6e204341202d20473331263024060355040b0c1d4170706c652043657274696669636174696f6e20417574686f7269747931133011060355040a0c0a4170706c6520496e632e310b30090603550406130255533059301306072a8648ce3d020106082a8648ce3d03010703420004f017118419d76485d51a5e25810776e880a2efde7bae4de08dfc4b93e13356d5665b35ae22d097760d224e7bba08fd7617ce88cb76bb6670bec8e82984ff5445a381f73081f4304606082b06010505070101043a3038303606082b06010505073001862a687474703a2f2f6f6373702e6170706c652e636f6d2f6f63737030342d6170706c65726f6f7463616733301d0603551d0e0416041423f249c44f93e4ef27e6c4f6286c3fa2bbfd2e4b300f0603551d130101ff040530030101ff301f0603551d23041830168014bbb0dea15833889aa48a99debebdebafdacb24ab30370603551d1f0430302e302ca02aa0288626687474703a2f2f63726c2e6170706c652e636f6d2f6170706c65726f6f74636167332e63726c300e0603551d0f0101ff0404030201063010060a2a864886f7636406020e04020500300a06082a8648ce3d040302036700306402303acf7283511699b186fb35c356ca62bff417edd90f754da28ebef19c815e42b789f898f79b599f98d5410d8f9de9c2fe0230322dd54421b0a305776c5df3383b9067fd177c2c216d964fc6726982126f54f87a7d1b99cb9b0989216106990f09921d00003182018c30820188020101308186307a312e302c06035504030c254170706c65204170706c69636174696f6e20496e746567726174696f6e204341202d20473331263024060355040b0c1d4170706c652043657274696669636174696f6e20417574686f7269747931133011060355040a0c0a4170706c6520496e632e310b300906035504061302555302082443f2a8069df577300d06096086480165030402010500a08195301806092a864886f70d010903310b06092a864886f70d010701301c06092a864886f70d010905310f170d3138303431383135343831385a302a06092a864886f70d010934311d301b300d06096086480165030402010500a10a06082a8648ce3d040302302f06092a864886f70d01090431220420bf34907f94b855ab98ab38cbfb57b658183dd81880a07259640ab5e5fef9d8ce300a06082a8648ce3d04030204473045022100a9829d882f2b7b400d3a7f734e3e6e15b69c214ef0f3e8344f4a25b78ee75dd4022001b6fb735770c43a5eefd3de308d1c4443276b2bfa56bdbf50fe8554d4a271ce000000000000\"\n" +
                    "}");
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity( sessionUrl, request , String.class );
            assertEquals(HttpStatus.OK, response.getStatusCode());
            System.out.println(response.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println(e.getResponseBodyAsString());
            throw e;
        }
    }
}
