package com.pj.squashrestapp.config.security.token;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenBlacklistedException extends AuthenticationException {

  public TokenBlacklistedException(final String message) {
    super(message);
  }

}
