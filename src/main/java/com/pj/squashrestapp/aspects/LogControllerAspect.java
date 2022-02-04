package com.pj.squashrestapp.aspects;

import static java.util.Arrays.asList;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.pj.squashrestapp.model.LogEntry;
import com.pj.squashrestapp.repositorymongo.LogEntryRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import com.yannbriancon.interceptor.HibernateQueryInterceptor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/** */
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
   * Logging aspect that matches all non-void repository and service methods.
   *
   * ONLY FOR DEBUG PURPOSES!!
   *
   * @param proceedingJoinPoint Spring method execution join point
   * @return unmodified return object from the controller method
   * @throws Throwable rethrows exception after logging it, so it can be passed to the client
   */
  //  @Around("utilMethodsPointcut() || repositoryMethodsPointcut() || serviceMethodsPointcut()")
  //  @Around("repositoryMethodsPointcut()")
  public Object logAllServiceAndRepositoryMethods(final ProceedingJoinPoint proceedingJoinPoint)
      throws Throwable {
    final MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
    final String className = methodSignature.getDeclaringType().getSimpleName();
    final String methodName = methodSignature.getName();

    hibernateQueryInterceptor.startQueryCount();

    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Object result = null;
    try {
      result = proceedingJoinPoint.proceed();
      stopWatch.stop();
      return result;

    } catch (final Throwable throwable) {
      log.error(throwable.getMessage(), throwable);
      throw throwable;

    } finally {
      log.info(
          "DEBUG\t{}\t{}ms\t{}.{}",
          hibernateQueryInterceptor.getQueryCount(),
          stopWatch.getTotalTimeMillis(),
          className,
          methodName);
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
  public Object logRedisServiceMethods(final ProceedingJoinPoint proceedingJoinPoint)
      throws Throwable {
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
          "REDIS-EVICT   {}  {}ms  {}.{}{}",
          username,
          stopWatch.getTotalTimeMillis(),
          className,
          methodName,
          Arrays.deepToString(args));
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
  @Around("controllerMethodsPointcut() || controllerDbInitMethodsPointcut() || serviceMethodsPointcut() || repositoryMethodsPointcut()")
  public Object logAllControllerAndRepositoriesMethods(final ProceedingJoinPoint proceedingJoinPoint)
      throws Throwable {
    final String username = GeneralUtil.extractSessionUsername();
    final Object[] args = proceedingJoinPoint.getArgs();

    final MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
    final Method method = methodSignature.getMethod();
    final String className = methodSignature.getDeclaringType().getSimpleName();
    final String methodName = methodSignature.getName();
    final boolean isSecretMethod = method.getAnnotation(SecretMethod.class) != null;

    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    final LogEntry logEntry = new LogEntry();
    logEntry.setTimestamp(LocalDateTime.now());
    logEntry.setMethodName(methodName);
    logEntry.setClassName(className);
    logEntry.setUsername(username);

    if (!isSecretMethod) {
      logEntry.setArguments(Arrays.deepToString(args));
    }

    if (className.endsWith("Controller")) {
      logEntry.setType("CONTROLLER");
    } else if (className.endsWith("Service")) {
      logEntry.setType("SERVICE");
    } else if (className.endsWith("Repository")) {
      logEntry.setType("REPOSITORY");
    }

    hibernateQueryInterceptor.startQueryCount();
    Object result;
    try {
      result = proceedingJoinPoint.proceed();
      stopWatch.stop();
      return result;

    } catch (final Throwable throwable) {
      log.error(throwable.getMessage(), throwable);
      logEntry.setErrorMessage(throwable.getMessage());
      logEntry.setStackTrace(Joiner
          .on("\n")
          .join(Iterables.limit(asList(throwable.getStackTrace()), 10)));
      throw throwable;

    } finally {
      final long totalTimeMillis = stopWatch.getTotalTimeMillis();
      final Long queryCount = hibernateQueryInterceptor.getQueryCount();

      log.info(
          "REST-REQUEST  {}  {}  {}ms  {}.{}{}",
          queryCount,
          username,
          totalTimeMillis,
          className,
          methodName,
          isSecretMethod ? "[**_SECRET_ARGUMENTS_**]" : Arrays.deepToString(args));

      logEntry.setDuration(totalTimeMillis);
      logEntry.setQueryCount(queryCount);

      logEntryRepository.save(logEntry);
    }
  }

}
