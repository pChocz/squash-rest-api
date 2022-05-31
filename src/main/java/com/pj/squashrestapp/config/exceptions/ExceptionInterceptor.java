package com.pj.squashrestapp.config.exceptions;

import com.pj.squashrestapp.config.ErrorResponse;
import com.pj.squashrestapp.hexagonal.email.EmailPrepareFacade;
import com.pj.squashrestapp.util.AuthorizationUtil;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Exception interceptors - this class defines what should specific exceptions return after request
 * is done (in terms of both response status and message).
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {

    private final EmailPrepareFacade emailPrepareFacade;

    /** https://mtyurt.net/post/spring-how-to-handle-ioexception-broken-pipe.html */
    @ExceptionHandler({
        IOException.class,
        NullPointerException.class,
    })
    ResponseEntity<ErrorResponse> handleIOException(final Exception ex, final HttpServletRequest request) {

        log.error("Caught IO/NullPointer Exception in the Exception Interceptor");

        final String rootCauseMessage = ExceptionUtils.getRootCauseMessage(ex);

        if (StringUtils.containsIgnoreCase(rootCauseMessage, "broken pipe")) {
            return null;

        } else {
            final HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

            sendExceptionEmail(ex, request, httpStatus);

            return new ResponseEntity<>(
                    ErrorResponse.builder()
                            .timestamp(LocalDateTime.now())
                            .message(ex.getMessage())
                            .status(httpStatus.value())
                            .path(request.getRequestURI())
                            .build(),
                    httpStatus);
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<ErrorResponse> handleNoSuchElementException(final Exception ex, final HttpServletRequest request) {

        log.error("Caught NoSuchElementException in the Exception Interceptor");

        final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(ex.getMessage())
                        .status(httpStatus.value())
                        .path(request.getRequestURI())
                        .build(),
                httpStatus);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            final Exception ex, final HttpServletRequest request) {

        final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        sendExceptionEmail(ex, request, httpStatus);

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(ErrorCode.WRONG_DATA_FORMAT)
                        .status(httpStatus.value())
                        .path(request.getRequestURI())
                        .build(),
                httpStatus);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> handleAccessDeniedException(final Exception ex, final HttpServletRequest request) {

        final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(ex.getMessage())
                        .status(httpStatus.value())
                        .path(request.getRequestURI())
                        .build(),
                httpStatus);
    }

    @ExceptionHandler({
        PasswordDoesNotMatchException.class,
        EmailAlreadyTakenException.class,
        GeneralBadRequestException.class
    })
    ResponseEntity<ErrorResponse> handleBadRequestException(final Exception ex, final HttpServletRequest request) {

        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(ex.getMessage())
                        .status(httpStatus.value())
                        .path(request.getRequestURI())
                        .build(),
                httpStatus);
    }

    @ExceptionHandler(WrongSignupDataException.class)
    ResponseEntity<ErrorResponse> handleNotAcceptableException(final Exception ex, final HttpServletRequest request) {

        final HttpStatus httpStatus = HttpStatus.NOT_ACCEPTABLE;

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(ex.getMessage())
                        .status(httpStatus.value())
                        .path(request.getRequestURI())
                        .build(),
                httpStatus);
    }

    /**
     * This method catches response status exceptions
     */
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ErrorResponse> handleResponseStatusException(final Exception ex, final HttpServletRequest request) {

        final HttpStatus httpStatus = ((ResponseStatusException) ex).getStatus();

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(((ResponseStatusException) ex).getReason())
                        .status(httpStatus.value())
                        .path(request.getRequestURI())
                        .build(),
                httpStatus);
    }

    /**
     * This method catches all exceptions that have not been specifically declared in above methods.
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleOtherException(final Exception ex, final HttpServletRequest request) {

        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        sendExceptionEmail(ex, request, httpStatus);

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .message(ex.getMessage())
                        .status(httpStatus.value())
                        .path(request.getRequestURI())
                        .build(),
                httpStatus);
    }

    private void sendExceptionEmail(final Exception ex, final HttpServletRequest request, final HttpStatus httpStatus) {
        final StringBuilder parametersBuilder = new StringBuilder();
        for (final String key : request.getParameterMap().keySet()) {
            final String[] valueArray = request.getParameterMap().get(key);
            final String values = StringUtils.join(valueArray, ", ");
            parametersBuilder.append(key).append(": ").append("[").append(values).append("], ");
        }
        final String parameters = parametersBuilder.substring(0, parametersBuilder.length() - 2);

        List<String> stackTrace = Arrays
                .stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .toList();

        final Map<String, Object> model = new HashMap<>();
        model.put("preheader", ex.getClass().getSimpleName());
        model.put("exception", ex.toString());
        model.put("user", GeneralUtil.extractSessionUsername());
        model.put("ip", AuthorizationUtil.extractRequestIpAddress());
        model.put("url", request.getRequestURL());
        model.put("type", request.getMethod());
        model.put("code", httpStatus.value());
        model.put("params", parameters);
        model.put("stackTrace", stackTrace);

        emailPrepareFacade.pushExceptionEmailToQueue(model);
    }
}
