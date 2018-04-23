package com.rbkmoney.provider.applepay.service;

import com.rbkmoney.provider.applepay.store.APCertStore;
import com.rbkmoney.woody.api.flow.error.WErrorDefinition;
import com.rbkmoney.woody.api.flow.error.WRuntimeException;
import com.rbkmoney.woody.api.trace.context.TraceContext;
import com.rbkmoney.woody.thrift.impl.http.error.THTransportErrorMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by vpankrashkin on 10.04.18.
 */
public class SessionService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final APCertStore certStore;
    private final int connTimeoutMs;
    private final int readTimeoutMs;
    private final int writeTimeoutMs;
    private final char[] identityPass;
    private final SSLProvider sslProvider;
    private final THTransportErrorMapper errorMapper;

    public SessionService(APCertStore certStore, String identityPass, int connTimeoutMs, int readTimeoutMs, int writeTimeoutMs) {
        this.certStore = certStore;
        this.connTimeoutMs = connTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
        this.writeTimeoutMs = writeTimeoutMs;
        this.identityPass = identityPass.toCharArray();
        this.sslProvider = new SSLProvider();
        this.errorMapper = new THTransportErrorMapper();
    }

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

    public String requestSession(String merchantId, String displayName, String domainName, String validationURL) throws CryptoException, CertNotFoundException, APSessionException, WRuntimeException {
        byte[] identityCert = certStore.getIdentityCert(merchantId);
        if (identityCert == null) {
            log.error("Identity cert not found for merchant: {}", merchantId);
            throw new CertNotFoundException("Not cert for merchant: " + merchantId);
        }

        try {
            OkHttpClient client = prepareClient(identityCert, identityPass);
            log.debug("Http client prepared");
            Request request = prepareRequest(merchantId, displayName, domainName, validationURL);
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
                .sslSocketFactory(sslProvider.getSSLForClient(identityCert, pass)).build();
    }

    private Request prepareRequest(String merchantId, String displayName, String domainName, String validationURL) {
        FormBody requestBody = new FormBody.Builder()
                .add("merchantIdentifier", merchantId)
                .add("displayName", displayName)
                .add("initiative", "web")
                .add("initiativeContext", domainName)
                .build();
        return new Request.Builder().url(prepareUrl(validationURL)).method("POST", requestBody).build();
    }

    private Request prepareRequest(String validationURL, String body) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-type: application/json;charset=utf-8"), body);
        return new Request.Builder().url(prepareUrl(validationURL)).method("POST", requestBody).build();
    }

    private String prepareUrl(String validationUrl) {
        return validationUrl;
    }

}
