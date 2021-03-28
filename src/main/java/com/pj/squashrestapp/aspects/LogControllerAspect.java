package com.pj.squashrestapp.aspects;

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

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogControllerAspect {

  private final HibernateQueryInterceptor hibernateQueryInterceptor;

  @Pointcut("execution(* com.pj.squashrestapp.controller.*.*(..)))")
  public void controllerMethodsPointcut() {
    // empty
  }

  @Pointcut("execution(* com.pj.squashrestapp.dbinit.controller.*.*(..)))")
  public void controllerDbInitMethodsPointcut() {
    // empty
  }

  /**
   * Logging aspect that matches all non-void controller methods.
   *
   * @param proceedingJoinPoint Spring method execution join point
   * @return unmodified return object from the controller method
   * @throws Throwable rethrows exception after logging it so it can be passed to the client
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
      log.info("REST-REQUEST\t{}\t{}\t{}ms\t{}.{}{}",
              hibernateQueryInterceptor.getQueryCount(),
              username,
              stopWatch.getTotalTimeMillis(),
              className, methodName, isSecretMethod ? "[**_SECRET_ARGUMENTS_**]" : Arrays.deepToString(args));
    }
  }

}
