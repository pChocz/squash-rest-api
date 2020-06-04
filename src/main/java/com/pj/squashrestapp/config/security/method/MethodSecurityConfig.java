package com.pj.squashrestapp.config.security.method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 *
 */
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

  @Autowired
  ApplicationContext applicationContext;

  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    final CustomMethodSecurityExpressionHandler handler = new CustomMethodSecurityExpressionHandler();
    handler.setApplicationContext(applicationContext);
    return handler;
  }

}

