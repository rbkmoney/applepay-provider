package com.rbkmoney.provider.applepay.service;

import com.rbkmoney.provider.applepay.store.APCertStore;
import com.rbkmoney.woody.api.flow.error.WErrorDefinition;
import com.rbkmoney.woody.api.flow.error.WRuntimeException;
import com.rbkmoney.woody.api.trace.context.TraceContext;
import com.rbkmoney.woody.thrift.impl.http.error.THTransportErrorMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class SessionService {

    private final APCertStore certStore;
    private final char[] identityPass;
    private final int connTimeoutMs;
    private final int readTimeoutMs;
    private final int writeTimeoutMs;
    private final DnsSelector dnsSelector;
    private final SSLProvider sslProvider = new SSLProvider();
    private final THTransportErrorMapper errorMapper = new THTransportErrorMapper();

    public String requestSession(String merchantId, String validationURL, String body) throws CryptoException, CertNotFoundException, APSessionException, WRuntimeException {
        byte[] identityCert = certStore.getIdentityCert(merchantId);
        if (identityCert == null) {
            log.error("Identity cert not found for merchant: {}", merchantId);
            throw new CertNotFoundException("Not cert for merchant: " + merchantId);
        }

        try {
            OkHttpClient client = prepareClient(identityCert, identityPass);
            log.debug("Http client prepared");
            Request request = prepareRequest(validationURL, body);
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new APSessionException("Request session failure", response.body().string());
            }
        } catch (IOException e) {
            WErrorDefinition errDef = errorMapper.mapToDef(e, TraceContext.getCurrentTraceData().getActiveSpan());
            if (errDef != null) {
                throw new WRuntimeException(errDef);
            } else {
                throw new WRuntimeException(e, new WErrorDefinition());
            }
        }
    }

    private OkHttpClient prepareClient(byte[] identityCert, char[] pass) throws CryptoException {
        return new OkHttpClient.Builder()
                .connectTimeout(connTimeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeoutMs, TimeUnit.MILLISECONDS)
                .addInterceptor(new HttpLoggingInterceptor())
                .sslSocketFactory(sslProvider.getSSLForClient(identityCert, pass), sslProvider.getX509TrustManager())
                .dns(dnsSelector)
                .build();
    }

    private Request prepareRequest(String validationURL, String body) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-type: application/json;charset=utf-8"), body);
        return new Request.Builder().url(prepareUrl(validationURL)).method("POST", requestBody).build();
    }

    private String prepareUrl(String validationUrl) {
        return validationUrl;
    }

}
