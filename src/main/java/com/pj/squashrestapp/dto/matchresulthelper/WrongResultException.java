package com.pj.squashrestapp.dto.matchresulthelper;

import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
public class WrongResultException extends RuntimeException {

    public WrongResultException(final String message) {
        super(message);
        log.error(message);
    }
}
