package com.pj.squashrestapp.hexagonal.healthcheck;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("healthcheck")
class HealthcheckConfig {

    @Setter
    @Getter
    @Value(value = "${url:}")
    private String url;
}
