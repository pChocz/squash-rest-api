package com.pj.squashrestapp.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 *
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {

  /**
   *
   * https://mtyurt.net/post/spring-how-to-handle-ioexception-broken-pipe.html
   *
   */
  @ExceptionHandler({
          IOException.class,
          NullPointerException.class,
  })
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  ResponseEntity<ErrorResponse> handleIOException(
          final Exception ex,
          final HttpServletRequest request) {

    log.error("Caught IO/NullPointer Exception in the Exception Interceptor");

    final String rootCauseMessage = ExceptionUtils.getRootCauseMessage(ex);

    if (StringUtils.containsIgnoreCase(rootCauseMessage, "broken pipe")) {
      return null;

    } else {
      final HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
      return new ResponseEntity<>(
              ErrorResponse.builder()
                      .timestamp(LocalDateTime.now())
                      .message(ex.getMessage())
                      .status(httpStatus.value())
                      .path(request.getRequestURI())
                      .build(),
              httpStatus
      );
    }
  }

  @ExceptionHandler(NoSuchElementException.class)
  ResponseEntity<ErrorResponse> handleNoSuchElementException(
          final Exception ex,
          final HttpServletRequest request) {

    final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    return new ResponseEntity<>(
            ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message(ex.getMessage())
                    .status(httpStatus.value())
                    .path(request.getRequestURI())
                    .build(),
            httpStatus
    );
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
          final Exception ex,
          final HttpServletRequest request) {

    final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    return new ResponseEntity<>(
            ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .message("Wrong data format. Are you sure you didn't type it yourself?")
                    .status(httpStatus.value())
                    .path(request.getRequestURI())
                    .build(),
            httpStatus
    );
  }

}