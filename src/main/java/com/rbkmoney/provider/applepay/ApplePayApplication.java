package com.rbkmoney.provider.applepay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(scanBasePackages = "com.rbkmoney.provider.applepay")
@ServletComponentScan
public class ApplePayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApplePayApplication.class, args);
    }
}
