package com.pj.squashrestapp.config.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@Slf4j
public class WrongSignupDataException extends RuntimeException {

  public WrongSignupDataException(final String message) {
    super(message);
    log.error(message);
  }

}
