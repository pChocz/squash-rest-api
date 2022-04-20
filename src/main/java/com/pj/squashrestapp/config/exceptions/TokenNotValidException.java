package com.pj.squashrestapp.config.exceptions;

import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public class TokenNotValidException extends RuntimeException {

    public TokenNotValidException(final String message) {
        super(message);
        log.error(message);
    }
}
