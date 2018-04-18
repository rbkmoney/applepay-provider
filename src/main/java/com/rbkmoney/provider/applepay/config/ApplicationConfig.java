package com.rbkmoney.provider.applepay.config;

import com.rbkmoney.provider.applepay.service.SSLProvider;
import com.rbkmoney.provider.applepay.service.SessionService;
import com.rbkmoney.provider.applepay.store.APCertStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by vpankrashkin on 10.04.18.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public APCertStore certStore(@Value("${cert.base:@null}") String baseDir, @Value("${cert.identity.path:@null}") String identityDir, @Value("${cert.processing.path:@null}") String processingDir) {
        if (baseDir == null && identityDir == null && processingDir == null) {
            throw new IllegalArgumentException("At least base cert dir must be specified");
        }
        if (identityDir != null && processingDir != null) {
            return new APCertStore(identityDir, processingDir);
        }

        return new APCertStore(baseDir);
    }

    @Bean
    public SessionService sessionService(APCertStore certStore,
                                         @Value("${cert.identity.pass}") String identityPass,
                                         @Value("${apple.conn_timeout}") int connTimeoutMs,
                                         @Value("${apple.read_timeout}") int readTimeoutMs,
                                         @Value("${apple.write_timeout}") int writeTimeoutMs) {
        return new SessionService(certStore, identityPass, connTimeoutMs, readTimeoutMs, writeTimeoutMs);
    }
}
