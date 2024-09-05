package com.najackdo.server.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "spring.jwt")
@Getter @Setter
public class JwtProperties {
    private String secret;
    private int accessExpire;
    private int refreshExpire;
}
