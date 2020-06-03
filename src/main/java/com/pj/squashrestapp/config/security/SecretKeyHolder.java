package com.pj.squashrestapp.config.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 *
 */
@Component
@Getter
public final class SecretKeyHolder {

  private final Key secretKey;

  @Autowired
  public SecretKeyHolder(@Value(value = "${jwt.secret:}") final String stringSecretKey) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(stringSecretKey));
  }

}
