package com.pj.squashrestapp.dbinit.service;

import com.pj.squashrestapp.dbinit.jsondto.JsonAdditionalMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonAll;
import com.pj.squashrestapp.dbinit.jsondto.JsonAuthorities;
import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonEmailChangeToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueRoles;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueRule;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueTrophy;
import com.pj.squashrestapp.dbinit.jsondto.JsonLostBall;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatchScore;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayerCredentials;
import com.pj.squashrestapp.dbinit.jsondto.JsonRefreshToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.dbinit.jsondto.JsonVerificationToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonXpPointsForRound;
import com.pj.squashrestapp.dbinit.jsondto.util.JsonImportUtil;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.enums.AuthorityType;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.EmailChangeToken;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.enums.LeagueRole;
import com.pj.squashrestapp.model.LeagueRule;
import com.pj.squashrestapp.model.LostBall;
import com.pj.squashrestapp.model.MatchScore;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RefreshToken;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.EmailChangeTokenRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RefreshTokenRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ex. of deserializing list with GSON:
 *
 * <p>final Type listOfMyClassObject = new TypeToken<ArrayList<JsonLeague>>() {}.getType(); final
 * List<JsonLeague> jsonLeagues = GsonUtil.gsonWithDateAndDateTime().fromJson(initLeagueJsonContent,
 * listOfMyClassObject);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInitializerService {

    private final XpPointsRepository xpPointsRepository;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleForLeagueRepository roleForLeagueRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;

    public boolean initialize(final String initAllJsonContent) {
        final Player adminPlayer = playerRepository.findByUsername("Admin");

        if (adminPlayer != null) {
            // means that Admin already exists so we cannot initialize it again
            return false;

        } else {
            log.info("Initializing - BEGIN");

            persistStandardAuthorities();

            final JsonAll jsonAll = GsonUtil.gsonPrettyWithDateAndDateTime().fromJson(initAllJsonContent, JsonAll.class);
            persistXpPointsFromJson(jsonAll.getXpPoints());
            persistLeaguesInitializeOnly(jsonAll.getLeagues());
            persistCredentials(jsonAll.getCredentials());
            persistAllLeaguesContent(jsonAll.getLeagues());
            persistRefreshTokens(jsonAll.getRefreshTokens());
            persistVerificationTokens(jsonAll.getVerificationTokens());
            persistEmailChangeTokens(jsonAll.getEmailChangeTokens());

            log.info("Initializing - FINISHED");
            return true;
        }
    }

    private void persistStandardAuthorities() {
        final Authority adminAuthority = new Authority(AuthorityType.ROLE_ADMIN);
        final Authority userAuthority = new Authority(AuthorityType.ROLE_USER);

        authorityRepository.save(adminAuthority);
        authorityRepository.save(userAuthority);
    }

    private void persistXpPointsFromJson(final List<JsonXpPointsForRound> jsonXpPointsForRoundAll) {
        final List<XpPointsForRound> xpPoints = new ArrayList<>();
        for (final JsonXpPointsForRound jsonXpPointsForRound : jsonXpPointsForRoundAll) {
            final String type = jsonXpPointsForRound.getType();
            final int[] players = jsonXpPointsForRound.buildPlayerSplitArray();
            final int[][] points = jsonXpPointsForRound.buildXpPointsArray();
            final XpPointsForRound xpPointsForRound = new XpPointsForRound(type, players, points);
            xpPoints.add(xpPointsForRound);
        }

        xpPointsRepository.saveAll(xpPoints);
    }

    private void persistLeaguesInitializeOnly(final List<JsonLeague> jsonLeagues) {
        for (final JsonLeague jsonLeague : jsonLeagues) {
            final League league = JsonImportUtil.constructLeague(jsonLeague);
            leagueRepository.save(league);
            createLeagueRoles(league);
        }
    }

    private void persistCredentials(final List<JsonPlayerCredentials> jsonCredentials) {
        for (final JsonPlayerCredentials credentials : jsonCredentials) {
            final Player player = new Player();
            player.setEnabled(true);
            player.setUsername(credentials.getUsername());
            player.setEmoji(credentials.getEmoji());
            player.setEmail(credentials.getEmail());
            player.setPassword(credentials.getPasswordHashed());
            player.setNonLocked(credentials.isNonLocked());
            player.setSuccessfulLoginAttempts(credentials.getSuccessfulLoginAttempts());
            player.setRegistrationDateTime(credentials.getRegistrationDateTime());
            player.setLastLoggedInDateTime(credentials.getLastLoggedInDateTime());
            player.setUuid(credentials.getUuid());
            player.setPasswordSessionUuid(credentials.getPasswordSessionUuid());
            player.setEnabled(credentials.isEnabled());
            player.setLocale(credentials.getLocale());
            player.setWantsEmails(credentials.getWantsEmails());

            playerRepository.save(player);
            persistLeagueRoles(credentials, player);
            persistAuthorities(credentials, player);
        }
    }

    private void persistAllLeaguesContent(final List<JsonLeague> jsonLeagues) {
        for (final JsonLeague jsonLeague : jsonLeagues) {
            final List<Player> allPlayers = playerRepository.findAll();

            final League league = buildLeague(jsonLeague, allPlayers);
            leagueRepository.save(league);
        }
    }

    private void persistRefreshTokens(final List<JsonRefreshToken> jsonRefreshTokens) {
        final List<RefreshToken> refreshTokens = new ArrayList<>();
        for (final JsonRefreshToken jsonRefreshToken : jsonRefreshTokens) {
            final Player player = playerRepository.findByUuid(jsonRefreshToken.getPlayerUuid());
            final RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(jsonRefreshToken.getToken());
            refreshToken.setPlayer(player);
            refreshToken.setExpirationDateTime(jsonRefreshToken.getExpirationDateTime());
            refreshTokens.add(refreshToken);
        }

        refreshTokenRepository.saveAll(refreshTokens);
    }

    private void persistVerificationTokens(final List<JsonVerificationToken> jsonVerificationTokens) {
        final List<VerificationToken> verificationTokens = new ArrayList<>();
        for (final JsonVerificationToken jsonVerificationToken : jsonVerificationTokens) {
            final Player player = playerRepository.findByUuid(jsonVerificationToken.getPlayerUuid());
            final VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(jsonVerificationToken.getToken());
            verificationToken.setPlayer(player);
            verificationToken.setExpirationDateTime(jsonVerificationToken.getExpirationDateTime());
            verificationTokens.add(verificationToken);
        }

        verificationTokenRepository.saveAll(verificationTokens);
    }

    private void persistEmailChangeTokens(final List<JsonEmailChangeToken> emailChangeTokens) {
        final List<EmailChangeToken> verificationTokens = new ArrayList<>();
        for (final JsonEmailChangeToken jsonEmailChangeToken : emailChangeTokens) {
            final Player player = playerRepository.findByUuid(jsonEmailChangeToken.getPlayerUuid());
            final EmailChangeToken emailChangeToken = new EmailChangeToken();
            emailChangeToken.setToken(jsonEmailChangeToken.getToken());
            emailChangeToken.setPlayer(player);
            emailChangeToken.setNewEmail(jsonEmailChangeToken.getNewEmail());
            emailChangeToken.setExpirationDateTime(jsonEmailChangeToken.getExpirationDateTime());
            verificationTokens.add(emailChangeToken);
        }

        emailChangeTokenRepository.saveAll(verificationTokens);
    }

    private void createLeagueRoles(final League league) {
        final RoleForLeague playerRole = new RoleForLeague(LeagueRole.PLAYER);
        league.addRoleForLeague(playerRole);
        roleForLeagueRepository.save(playerRole);

        final RoleForLeague moderatorRole = new RoleForLeague(LeagueRole.MODERATOR);
        league.addRoleForLeague(moderatorRole);
        roleForLeagueRepository.save(moderatorRole);

        final RoleForLeague ownerRole = new RoleForLeague(LeagueRole.OWNER);
        league.addRoleForLeague(ownerRole);
        roleForLeagueRepository.save(ownerRole);
    }

    private void persistLeagueRoles(final JsonPlayerCredentials simpleCredentials, final Player player) {
        final List<JsonLeagueRoles> jsonLeagueRoles = simpleCredentials.getLeagueRoles();
        for (final JsonLeagueRoles jsonLeagueRole : jsonLeagueRoles) {
            final String leagueName = jsonLeagueRole.getLeague();
            final String roleAsString = jsonLeagueRole.getRole();

            final League league = leagueRepository.findByName(leagueName);
            final LeagueRole role = LeagueRole.valueOf(roleAsString);

            final RoleForLeague leagueRole = roleForLeagueRepository.findByLeagueAndLeagueRole(league, role);
            player.addRole(leagueRole);
            roleForLeagueRepository.save(leagueRole);
        }
    }

    private void persistAuthorities(final JsonPlayerCredentials credentials, final Player player) {
        final List<JsonAuthorities> jsonAuthorities = credentials.getAuthorities();
        for (final JsonAuthorities jsonAuthority : jsonAuthorities) {
            final String authorityAsString = jsonAuthority.getAuthority();
            final AuthorityType authorityType = AuthorityType.valueOf(authorityAsString);
            final Authority authority = authorityRepository.findByType(authorityType);
            player.addAuthority(authority);
            authorityRepository.save(authority);
        }
    }

    @Transactional
    public League buildLeague(final JsonLeague jsonLeague, final List<Player> players) {
        final League league = leagueRepository.findByName(jsonLeague.getName());

        for (final JsonLeagueTrophy jsonLeagueTrophy : jsonLeague.getTrophies()) {
            final TrophyForLeague trophyForLeague = JsonImportUtil.constructLeagueTrophy(jsonLeagueTrophy, players);
            league.addTrophyForLeague(trophyForLeague);
        }

        for (final JsonLeagueRule rule : jsonLeague.getRules()) {
            final LeagueRule leagueRule = new LeagueRule();
            leagueRule.setUuid(rule.getUuid());
            leagueRule.setOrderValue(rule.getOrderValue());
            leagueRule.setType(rule.getType());
            leagueRule.setRule(rule.getRule());
            leagueRule.setAudit(rule.getAudit());
            league.addRuleForLeague(leagueRule);
        }

        for (final JsonAdditionalMatch jsonAdditionalMatch : jsonLeague.getAdditionalMatches()) {
            final AdditionalMatch additionalMatch = JsonImportUtil.constructAdditionalMatch(jsonAdditionalMatch, players);

            for (final JsonSetResult jsonSetResult : jsonAdditionalMatch.getSets()) {
                final AdditionalSetResult setResult = JsonImportUtil.constructAdditionalSetResult(jsonSetResult);
                additionalMatch.addSetResult(setResult);
            }

            league.addAdditionalMatch(additionalMatch);
        }

        for (final JsonSeason jsonSeason : jsonLeague.getSeasons()) {
            final Season season = JsonImportUtil.constructSeason(jsonSeason);

            for (final JsonBonusPoint jsonBonusPoint : jsonSeason.getBonusPoints()) {
                final BonusPoint bonusPoint = JsonImportUtil.constructBonusPoints(jsonBonusPoint, players);
                season.addBonusPoint(bonusPoint);
            }

            for (final JsonLostBall jsonLostBall : jsonSeason.getLostBalls()) {
                final LostBall lostBall = JsonImportUtil.constructLostBalls(jsonLostBall, players);
                season.addLostBall(lostBall);
            }

            for (final JsonRound jsonRound : jsonSeason.getRounds()) {
                final Round round = JsonImportUtil.constructRound(jsonRound);

                for (final JsonRoundGroup jsonRoundGroup : jsonRound.getGroups()) {
                    final RoundGroup roundGroup = JsonImportUtil.constructRoundGroup(jsonRoundGroup);

                    for (final JsonMatch jsonMatch : jsonRoundGroup.getMatches()) {
                        final Match match = JsonImportUtil.constructMatch(jsonMatch, players);

                        for (final JsonSetResult jsonSetResult : jsonMatch.getSets()) {
                            final SetResult setResult = JsonImportUtil.constructSetResult(jsonSetResult);
                            match.addSetResult(setResult);
                        }

                        for (final JsonMatchScore jsonMatchScore : jsonMatch.getMatchScores()) {
                            final MatchScore matchScore = JsonImportUtil.constructMatchScore(jsonMatchScore);
                            match.addScore(matchScore);
                        }

                        roundGroup.addMatch(match);
                    }
                    round.addRoundGroup(roundGroup);
                }
                JsonImportUtil.setSplitForRound(round);
                season.addRound(round);
            }
            league.addSeason(season);
        }
        return league;
    }
}
