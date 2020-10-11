package com.pj.squashrestapp.config.security.token;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

/**
 * Helper class that extracts jwt.secret key from the application.yml
 * file and builds cryptographic key needed for the authenticaton and
 * autorization purposes within the Spring Security framework.
 */
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("jwt")
public class SecretKeyHolder {

  @Value(value = "${secret:}")
  private String secret;

  public Key getSecretKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

}
