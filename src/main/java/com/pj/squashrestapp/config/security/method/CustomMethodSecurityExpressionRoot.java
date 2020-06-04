package com.pj.squashrestapp.config.security.method;

import com.pj.squashrestapp.config.UserDetailsImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Main class that provides access to specific Entities. It provides
 * methods for securing RestController methods by Spring Security
 * annotations.
 *
 * NOTE:
 * Methods from this class are called by annotations only, so they are
 * not tracked natively by IDE and must be manually searched for within
 * the {@link com.pj.squashrestapp.controller} package.
 */
@SuppressWarnings("unused")
public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

  private final UserDetailsImpl principal;

  @Getter
  @Setter
  private Object filterObject;

  @Getter
  @Setter
  private Object returnObject;

  /**
   * Constructor assigns principal field based on
   * the authentication of current session.
   *
   * @param authentication authentication object
   */
  public CustomMethodSecurityExpressionRoot(final Authentication authentication) {
    super(authentication);
    this.principal = (UserDetailsImpl) authentication.getPrincipal();
  }

  @Override
  public Object getThis() {
    return null;
  }

  public boolean hasRoleForLeague(final Long leagueId, final String role) {
    if (principal.isAdmin()) {
      return true;
    }
    return principal.hasRoleForLeague(leagueId, role);
  }

}
