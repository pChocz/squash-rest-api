package com.pj.squashrestapp.config.security.method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

/**
 * Class that allows to implement custom method security logic.
 * Implementation is done by {@link CustomMethodSecurityExpressionRoot} class.
 */
public class CustomMethodSecurityExpressionHandler
        extends DefaultMethodSecurityExpressionHandler {

  private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

  @Override
  protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
          final Authentication authentication,
          final MethodInvocation invocation) {
    final CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication);
    root.setPermissionEvaluator(getPermissionEvaluator());
    root.setTrustResolver(this.trustResolver);
    root.setRoleHierarchy(getRoleHierarchy());
    return root;
  }

}
