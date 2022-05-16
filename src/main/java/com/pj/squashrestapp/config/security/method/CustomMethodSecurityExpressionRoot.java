package com.pj.squashrestapp.config.security.method;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.dto.match.AdditionalMatchSimpleDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.BonusPointRepository;
import com.pj.squashrestapp.repository.LeagueRulesRepository;
import com.pj.squashrestapp.repository.LostBallRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.util.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.Set;
import java.util.UUID;

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
    private LostBallRepository lostBallRepository;
    private LeagueRulesRepository leagueRulesRepository;
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

    public boolean hasRoleForLostBall(final UUID lostBallUuid, final LeagueRole role) {
        if (principal.isAdmin()) {
            return true;
        }
        final UUID leagueUuid = lostBallRepository.retrieveLeagueUuidOfLostBall(lostBallUuid);
        return principal.hasRoleForLeague(leagueUuid, role);
    }

    public boolean isMatchFinished(final UUID matchUuid) {
        final Match match = matchRepository.findMatchByUuid(matchUuid).orElseThrow();
        return new MatchSimpleDto(match).checkFinished();
    }

    public boolean isAdditionalMatchFinished(final UUID matchUuid) {
        final AdditionalMatch match = additionalMatchRepository.findByUuid(matchUuid).orElseThrow();
        return new AdditionalMatchSimpleDto(match).checkFinished();
    }

    public boolean isPlayerOfRound(final UUID roundUuid, final UUID playerUuid) {
        if (principal.isAdmin()) {
            return true;
        }
        return roundRepository.checkIfPlayerOfRound(roundUuid, playerUuid);
    }

    public boolean isPlayerOfRoundForMatch(final UUID matchUuid) {
        if (principal.isAdmin()) {
            return true;
        }
        final UUID playerUuid = principal.getUuid();
        final UUID roundUuid = matchRepository
                .findMatchByUuid(matchUuid)
                .orElseThrow()
                .getRoundGroup()
                .getRound()
                .getUuid();
        final boolean isPlayerOfRound = roundRepository.checkIfPlayerOfRound(roundUuid, playerUuid);
        if (!isPlayerOfRound) {
            throw new AccessDeniedException(ErrorCode.NOT_A_PLAYER_OF_LEAGUE);
        }
        return true;
    }

    public boolean hasRoleForLeagueRule(final UUID leagueRuleUuid, final LeagueRole role) {
        if (principal.isAdmin()) {
            return true;
        }
        final UUID leagueUuid = leagueRulesRepository.retrieveLeagueUuidOfRule(leagueRuleUuid);
        return principal.hasRoleForLeague(leagueUuid, role);
    }

    public boolean hasRoleForMatch(final UUID matchUuid, final LeagueRole role) {
        if (principal.isAdmin()) {
            return true;
        }
        final UUID leagueUuid = matchRepository.retrieveLeagueUuidOfMatch(matchUuid);
        return principal.hasRoleForLeague(leagueUuid, role);
    }

    public boolean hasRoleForAdditionalMatch(final UUID matchUuid, final LeagueRole role) {
        if (principal.isAdmin()) {
            return true;
        }
        final UUID leagueUuid = additionalMatchRepository
                .findByUuid(matchUuid)
                .orElseThrow()
                .getLeague()
                .getUuid();
        return principal.hasRoleForLeague(leagueUuid, role);
    }

    public boolean isOneOfThePlayers(final UUID firstPlayerUuid, final UUID secondPlayerUuid) {
        if (principal.isAdmin()) {
            return true;
        }
        return Set.of(firstPlayerUuid, secondPlayerUuid).contains(principal.getUuid());
    }

    public boolean isPlayerOfMatch(final UUID matchUuid) {
        if (principal.isAdmin()) {
            return true;
        }
        final Match match = matchRepository.findMatchByUuid(matchUuid).orElseThrow();
        final Set<UUID> playersUuids = Set.of(match.getFirstPlayer().getUuid(), match.getSecondPlayer().getUuid());
        return playersUuids.contains(principal.getUuid());
    }

    public boolean isPlayerOfAdditionalMatch(final UUID matchUuid) {
        if (principal.isAdmin()) {
            return true;
        }
        final AdditionalMatch match = additionalMatchRepository.findByUuid(matchUuid).orElseThrow();
        final Set<UUID> playersUuids = Set.of(match.getFirstPlayer().getUuid(), match.getSecondPlayer().getUuid());
        return playersUuids.contains(principal.getUuid());
    }
}
