package com.rbkmoney.provider.applepay.config;

import com.rbkmoney.provider.applepay.iface.session.DumbRequestSessionController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(DumbRequestSessionController.class.getPackage().getName()))
                .paths(PathSelectors.any())
                .build();
        docket.produces(Set.of(MediaType.APPLICATION_JSON_VALUE));
        docket.forCodeGeneration(true);
        return docket;
    }
}