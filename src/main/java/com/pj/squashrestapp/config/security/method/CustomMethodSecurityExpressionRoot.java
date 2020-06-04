package com.pj.squashrestapp.config.security.method;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
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
@Getter
@Setter
public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot
        implements MethodSecurityExpressionOperations {

  private final UserDetailsImpl principal;

  private MatchRepository matchRepository;
  private RoundRepository roundRepository;

  private Object target;
  private Object filterObject;
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
    return target;
  }

  void setThis(final Object target) {
    this.target = target;
  }

  public boolean hasRoleForLeague(final Long leagueId, final String role) {
    if (principal.isAdmin()) {
      return true;
    }
    return principal.hasRoleForLeague(leagueId, role);
  }

  public boolean hasRoleForMatch(final Long matchId, final String role) {
    if (principal.isAdmin()) {
      return true;
    }
    final Long leagueId = matchRepository.retrieveLeagueIdOfMatch(matchId);
    return principal.hasRoleForLeague(leagueId, role);
  }

}
