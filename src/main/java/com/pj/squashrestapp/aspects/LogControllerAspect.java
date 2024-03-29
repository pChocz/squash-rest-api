package com.pj.squashrestapp.aspects;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.pj.squashrestapp.mongologs.LogEntry;
import com.pj.squashrestapp.mongologs.LogEntryRepository;
import com.pj.squashrestapp.mongologs.LogType;
import com.pj.squashrestapp.util.AuthorizationUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import com.yannbriancon.interceptor.HibernateQueryInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static net.logstash.logback.argument.StructuredArguments.kv;
import static net.logstash.logback.argument.StructuredArguments.v;

/** */
@SuppressWarnings("PlaceholderCountMatchesArgumentCount")
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogControllerAspect {

    private final HibernateQueryInterceptor hibernateQueryInterceptor;
    private final LogEntryRepository logEntryRepository;

    @Pointcut("execution(* com.pj.squashrestapp.util.*.*(..)))")
    public void utilMethodsPointcut() {
        // empty
    }

    @Pointcut("execution(* com.pj.squashrestapp.repository.*.*(..)))")
    public void repositoryMethodsPointcut() {
        // empty
    }

    @Pointcut("execution(* com.pj.squashrestapp.service.*.*(..)))")
    public void serviceMethodsPointcut() {
        // empty
    }

    @Pointcut("execution(* com.pj.squashrestapp.service.RedisCacheService.*(..)))")
    public void redisServiceMethodsPointcut() {
        // empty
    }

    @Pointcut("execution(* com.pj.squashrestapp.controller.*.*(..)))")
    public void controllerMethodsPointcut() {
        // empty
    }

    @Pointcut("execution(* com.pj.squashrestapp.dbinit.controller.*.*(..)))")
    public void controllerDbInitMethodsPointcut() {
        // empty
    }

    /**
     * Logging aspect that matches all service methods.
     *
     * @param proceedingJoinPoint Spring method execution join point
     * @return unmodified return object from the controller method
     * @throws Throwable rethrows exception after logging it, so it can be passed to the client
     */
    @Around("serviceMethodsPointcut()")
    public Object logAllServiceAndRepositoryMethods(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        final String className = methodSignature.getDeclaringType().getSimpleName();
        final String methodName = methodSignature.getName();

        hibernateQueryInterceptor.startQueryCount();

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result;
        try {
            log.info(
                    "Entering service method {}.{}",
                    v("className", className),
                    v("methodName", methodName)
            );
            result = proceedingJoinPoint.proceed();
            stopWatch.stop();
            return result;

        } catch (final Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            throw throwable;

        } finally {
            log.info(
                    "Exiting service method {}.{} | {} | {}",
                    v("className", className),
                    v("methodName", methodName),
                    kv("queries", hibernateQueryInterceptor.getQueryCount()),
                    kv("timeMillis", stopWatch.getTotalTimeMillis())
            );
        }
    }

    /**
     * Logging aspect that matches all redis-evict related methods.
     *
     * @param proceedingJoinPoint Spring method execution join point
     * @return unmodified return object from the controller method
     * @throws Throwable rethrows exception after logging it, so it can be passed to the client
     */
    @Around("redisServiceMethodsPointcut()")
    public Object logRedisServiceMethods(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final String username = GeneralUtil.extractSessionUsername();
        final Object[] args = proceedingJoinPoint.getArgs();

        final MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        final String className = methodSignature.getDeclaringType().getSimpleName();
        final String methodName = methodSignature.getName();

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result;
        try {
            result = proceedingJoinPoint.proceed();
            stopWatch.stop();
            return result;

        } catch (final Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            throw throwable;

        } finally {
            log.info(
                    "REDIS-EVICT {}.{}({}) | {}",
                    v("className", className),
                    v("methodName", methodName),
                    v("arguments", customArrayDeepToString(args)),
                    kv("timeMillis", stopWatch.getTotalTimeMillis())
            );
        }
    }

    /**
     * Logging aspect that matches all controller methods.
     *
     * It logs to Mongo DB as well.
     *
     * @param proceedingJoinPoint Spring method execution join point
     * @return unmodified return object from the controller method
     * @throws Throwable rethrows exception after logging it, so it can be passed to the client
     */
    @Around("controllerMethodsPointcut() || controllerDbInitMethodsPointcut()")
    public Object logAllControllerMethods(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final String username = GeneralUtil.extractSessionUsername();
        final Object[] args = proceedingJoinPoint.getArgs();

        final MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        final Method method = methodSignature.getMethod();
        final String className = methodSignature.getDeclaringType().getSimpleName();
        final String methodName = methodSignature.getName();
        final boolean isSecretMethod = method.getAnnotation(SecretMethod.class) != null;
        final boolean logResultIgnore = method.getAnnotation(LogResultIgnore.class) != null;
        final String arguments = isSecretMethod ? "[**_SECRET_ARGUMENTS_**]" : customArrayDeepToString(args);

        final String requestMapping = method.getAnnotation(GetMapping.class) != null
                ? "GET"
                : method.getAnnotation(PutMapping.class) != null
                        ? "PUT"
                        : method.getAnnotation(PostMapping.class) != null
                                ? "POST"
                                : method.getAnnotation(DeleteMapping.class) != null
                                        ? "DELETE"
                                        : null;

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final LogEntry logEntry = new LogEntry();
        logEntry.setTimestamp(Date.from(Instant.now(Clock.systemUTC())));
        logEntry.setMethodName(methodName);
        logEntry.setClassName(className);
        logEntry.setUsername(username);
        logEntry.setIsException(false);
        logEntry.setType(LogType.CONTROLLER);
        logEntry.setArguments(arguments);
        if (requestMapping != null) {
            logEntry.setRequestMapping(requestMapping);
        }

        hibernateQueryInterceptor.startQueryCount();
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
            logEntry.setMessage(className + "." + methodName + "(" + arguments + ")");
            return result;

        } catch (final Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            logEntry.setIsException(true);
            logEntry.setMessage(className + "." + methodName + arguments
                    + "\n" + throwable.getMessage()
                    + "\n" + Joiner.on("\n").join(Iterables.limit(asList(throwable.getStackTrace()), 20)));
            throw throwable;

        } finally {
            stopWatch.stop();
            final long totalTimeMillis = stopWatch.getTotalTimeMillis();
            final Long queryCount = hibernateQueryInterceptor.getQueryCount();

            logEntry.setDuration(totalTimeMillis);
            logEntry.setQueryCount(queryCount);
            logEntryRepository.save(logEntry);

            log.info(
                    "REST-REQUEST {} | {}.{}({}) | {} | {} | {} | {}",
                    v("requestMapping", requestMapping),
                    v("className", className),
                    v("methodName", methodName),
                    v("arguments", arguments),
                    v("username", username),
                    kv("ipAddress", AuthorizationUtil.extractRequestIpAddress()),
                    kv("queries", queryCount),
                    kv("timeMillis", totalTimeMillis),
                    v("result", result == null
                            ? "NO_RESULT"
                            : logResultIgnore
                                ? "IGNORED"
                                : result
                    )
            );
        }
    }

    @SuppressWarnings("unchecked")
    private String customArrayDeepToString(Object[] args) {
        return args.length == 0
                ? null
                : Arrays.stream(args)
                        .map(arg -> {
                            if (arg instanceof List list && !list.isEmpty() && list.get(0) instanceof UUID[]) {
                                return ((List<UUID[]>) arg)
                                        .stream().map(Arrays::deepToString).collect(Collectors.joining(", ", "[", "]"));

                            } else if (arg instanceof Object[] argArray) {
                                return Arrays.deepToString(argArray);

                            } else if (arg == null) {
                                return "null";

                            } else if (arg instanceof int[] argArray) {
                                return Arrays.toString(argArray);

                            } else if (arg instanceof long[] argArray) {
                                return Arrays.toString(argArray);

                            } else {
                                return arg.toString();
                            }
                        })
                        .collect(Collectors.joining(", "));
    }
}
