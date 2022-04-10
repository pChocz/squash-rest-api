package com.pj.squashrestapp.config.security.method;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.BonusPointRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Main class that provides access to specific Entities. It provides methods for securing
 * RestController methods by Spring Security annotations.
 *
 * <p>NOTE: Methods from this class are called by annotations only, so they are not tracked natively
 * by IDE and must be manually searched for within the {@link com.pj.squashrestapp.controller}
 * package.
 */
@SuppressWarnings("unused")
@Getter
@Setter
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

  private final UserDetailsImpl principal;

  private MatchRepository matchRepository;
  private RoundRepository roundRepository;
  private SeasonRepository seasonRepository;
  private BonusPointRepository bonusPointRepository;
  private AdditionalMatchRepository additionalMatchRepository;

  private Object target;
  private Object filterObject;
  private Object returnObject;

  /**
   * Constructor assigns principal field based on the authentication of current session.
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

  public boolean isAdmin() {
    return principal.isAdmin();
  }

  public boolean isOwnerOfLeague(final UUID leagueUuid) {
    if (principal.isAdmin()) {
      return true;
    }
    return principal.hasRoleForLeague(leagueUuid, LeagueRole.OWNER);
  }

  public boolean hasRoleForLeague(final UUID leagueUuid, final LeagueRole role) {
    if (principal.isAdmin()) {
      return true;
    }
    return principal.hasRoleForLeague(leagueUuid, role);
  }

  public boolean hasRoleForSeason(final UUID seasonUuid, final LeagueRole role) {
    if (principal.isAdmin()) {
      return true;
    }
    final UUID leagueUuid = seasonRepository.retrieveLeagueUuidOfSeason(seasonUuid);
    return principal.hasRoleForLeague(leagueUuid, role);
  }

  public boolean hasRoleForRound(final UUID roundUuid, final LeagueRole role) {
    if (principal.isAdmin()) {
      return true;
    }
    final UUID leagueUuid = roundRepository.retrieveLeagueUuidOfRound(roundUuid);
    return principal.hasRoleForLeague(leagueUuid, role);
  }

  public boolean hasRoleForBonusPoint(final UUID bonusPointUuid, final LeagueRole role) {
    if (principal.isAdmin()) {
      return true;
    }
    final UUID leagueUuid = bonusPointRepository.retrieveLeagueUuidOfBonusPoint(bonusPointUuid);
    return principal.hasRoleForLeague(leagueUuid, role);
  }

  public boolean hasRoleForMatch(final UUID matchUuid, final LeagueRole role) {
    if (principal.isAdmin()) {
      return true;
    }
    final UUID leagueUuid = matchRepository.retrieveLeagueUuidOfMatch(matchUuid);
    return principal.hasRoleForLeague(leagueUuid, role);
  }

  public boolean isOneOfThePlayers(final UUID firstPlayerUuid, final UUID secondPlayerUuid) {
    if (principal.isAdmin()) {
      return true;
    }
    return principal.getUuid().equals(firstPlayerUuid)
        || principal.getUuid().equals(secondPlayerUuid);
  }

  public boolean isPlayerOfAdditionalMatch(final UUID matchUuid) {
    if (principal.isAdmin()) {
      return true;
    }
    final Optional<AdditionalMatch> match = additionalMatchRepository.findByUuid(matchUuid);
    return principal.getUuid().equals(match.get().getFirstPlayer().getUuid())
        || principal.getUuid().equals(match.get().getSecondPlayer().getUuid());
  }
}
