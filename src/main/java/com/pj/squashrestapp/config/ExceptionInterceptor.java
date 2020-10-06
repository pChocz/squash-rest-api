package com.pj.squashrestapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

  @ExceptionHandler(NullPointerException.class)
  ResponseEntity<ErrorResponse> handleNullPointerException(
          final Exception ex,
          final HttpServletRequest request) {

    log.error("Caught NullPointerException in the Exception Interceptor");

    final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

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

  @ExceptionHandler(IOException.class)
  ResponseEntity<ErrorResponse> handleIOException(
          final Exception ex,
          final HttpServletRequest request) {

    log.error("Caught IOException in the Exception Interceptor");

    final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

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
