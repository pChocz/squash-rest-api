package com.pj.squashrestapp.aspects;

import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 *
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogQueryAspect {

  @Pointcut("execution(@com.pj.squashrestapp.aspects.QueryLog * * (..))")
  public void loggableQueryMethodsPointcut() {
    // empty
  }

  @AfterReturning(pointcut = "loggableQueryMethodsPointcut()", returning = "result")
  public void logQueries(final JoinPoint joinPoint, final Object result) {
    if (result instanceof LoggableQuery) {
      log.info("QUERY\t{}\t{}",
              GeneralUtil.extractSessionUsername(),
              ((LoggableQuery) result).message());
    }
  }

}
