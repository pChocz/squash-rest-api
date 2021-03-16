package com.pj.squashrestapp.dbinit.service;

import com.pj.squashrestapp.dbinit.jsondto.JsonAuthorities;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueRoles;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayerCredentials;
import com.pj.squashrestapp.dbinit.jsondto.JsonRefreshToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonVerificationToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonXpPointsForRound;
import com.pj.squashrestapp.dbinit.jsondto.util.JsonExportUtil;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RefreshToken;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.repository.BonusPointRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RefreshTokenRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import com.pj.squashrestapp.service.XpPointsService;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

  private final SetResultRepository setResultRepository;
  private final SeasonRepository seasonRepository;
  private final LeagueRepository leagueRepository;
  private final PlayerRepository playerRepository;
  private final RoundRepository roundRepository;
  private final BonusPointRepository bonusPointRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final XpPointsService xpPointsService;

  public JsonSeason seasonToJson(final UUID seasonUuid) {
    final List<SetResult> setResults = setResultRepository.fetchBySeasonUuid(seasonUuid);
    final Long seasonId = seasonRepository.findIdByUuid(seasonUuid);
    final Season season = EntityGraphBuildUtil.reconstructSeason(setResults, seasonId);
    final List<BonusPoint> bonusPoints = bonusPointRepository.findBySeasonUuid(seasonUuid);
    final JsonSeason seasonJson = JsonExportUtil.buildSeasonJson(season, bonusPoints);
    return seasonJson;
  }

  public JsonRound roundToJson(final UUID roundUuid) {
    final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(roundUuid);
    final Long roundId = roundRepository.findIdByUuid(roundUuid);
    final Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
    final JsonRound roundJson = JsonExportUtil.buildRoundJson(round);
    return roundJson;
  }

  public List<JsonXpPointsForRound> allXpPoints() {
    final List<XpPointsForRound> xpPointsForRoundAll = xpPointsService.buildAllAsNativeObject();

    final List<JsonXpPointsForRound> jsonXpPointsForRoundAll = new ArrayList<>();
    for (final XpPointsForRound xpPointsForRound : xpPointsForRoundAll) {

      final String splitCsv = xpPointsForRound.getSplit().replace(" | ", ", ");

      final List<String> pointsCsv = xpPointsForRound
              .getXpPointsForRoundGroups()
              .stream()
              .map(xpPointsForRoundGroup -> xpPointsForRoundGroup
                      .getXpPointsForPlaces()
                      .stream()
                      .map(xpPointsForPlace -> String.valueOf(xpPointsForPlace.getPoints()))
                      .collect(Collectors.toList()))
              .map(roundGroupPointsList -> String.join(", ", roundGroupPointsList))
              .collect(Collectors.toList());

      final JsonXpPointsForRound jsonXpPointsForRound = new JsonXpPointsForRound();
      jsonXpPointsForRound.setType(xpPointsForRound.getType());
      jsonXpPointsForRound.setNumberOfPlayersCsv(splitCsv);
      jsonXpPointsForRound.setPointsCsv(pointsCsv);
      jsonXpPointsForRoundAll.add(jsonXpPointsForRound);
    }

    return jsonXpPointsForRoundAll;
  }

  @Transactional
  public List<JsonLeague> allLeagues() {
    final List<UUID> allLeaguesUuids = leagueRepository.findUuids();
    log.info("Backing up {} leagues", allLeaguesUuids.size());
    final List<JsonLeague> jsonLeagues = allLeaguesUuids
            .stream()
            .map(this::leagueToJson)
            .collect(Collectors.toList());
    log.info("Finished backing up all leagues");
    return jsonLeagues;
  }

  @Transactional
  public JsonLeague leagueToJson(final UUID leagueUuid) {
    final String leagueName = leagueRepository.findByUuid(leagueUuid).orElseThrow().getName();
    log.info("\tBacking up league -> {}", leagueName);
    final League league = leagueRepository.findByUuidForBackup(leagueUuid).orElseThrow();
    final List<BonusPoint> bonusPoints = bonusPointRepository.findByLeagueUuid(league.getUuid());
    final JsonLeague leagueJson = JsonExportUtil.buildLeagueJson(league, bonusPoints);
    log.info("\tFinished backing up league -> {}", league.getName());
    return leagueJson;
  }

  public List<JsonPlayerCredentials> allPlayersCredentials() {
    final List<JsonPlayerCredentials> playerCredentials = new ArrayList<>();
    final List<Player> players = playerRepository.findAll();
    for (final Player player : players) {
      playerCredentials.add(buildPlayerCredentialsJson(player));
    }
    return playerCredentials;
  }

  private JsonPlayerCredentials buildPlayerCredentialsJson(final Player player) {
    final JsonPlayerCredentials jsonPlayerCredentials = new JsonPlayerCredentials();
    jsonPlayerCredentials.setUsername(player.getUsername());
    jsonPlayerCredentials.setPasswordHashed(player.getPassword());
    jsonPlayerCredentials.setEmail(player.getEmail());
    jsonPlayerCredentials.setUuid(player.getUuid());
    jsonPlayerCredentials.setPasswordSessionUuid(player.getPasswordSessionUuid());
    jsonPlayerCredentials.setAuthorities(buildPlayerAuthoritiesJson(player));
    jsonPlayerCredentials.setLeagueRoles(buildPlayerLeagueRolesJson(player));
    return jsonPlayerCredentials;
  }

  private List<JsonAuthorities> buildPlayerAuthoritiesJson(final Player player) {
    final List<JsonAuthorities> jsonAuthorities = new ArrayList<>();
    for (final Authority authority : player.getAuthorities()) {
      final JsonAuthorities jsonAuthority = new JsonAuthorities();
      jsonAuthority.setAuthority(authority.getType().name());
      jsonAuthorities.add(jsonAuthority);
    }
    return jsonAuthorities;
  }

  private List<JsonLeagueRoles> buildPlayerLeagueRolesJson(final Player player) {
    final List<JsonLeagueRoles> jsonLeagueRoles = new ArrayList<>();
    for (final RoleForLeague role : player.getRoles()) {
      final JsonLeagueRoles jsonLeagueRole = new JsonLeagueRoles();
      jsonLeagueRole.setLeague(role.getLeague().getName());
      jsonLeagueRole.setRole(role.getLeagueRole().name());
      jsonLeagueRoles.add(jsonLeagueRole);
    }
    return jsonLeagueRoles;
  }

  public List<JsonRefreshToken> allRefreshTokens() {
    final List<JsonRefreshToken> jsonRefreshTokens = new ArrayList<>();
    final List<RefreshToken> allRefreshTokens = refreshTokenRepository.findAll();
    for (final RefreshToken refreshToken : allRefreshTokens) {
      final JsonRefreshToken jsonRefreshToken = new JsonRefreshToken();
      jsonRefreshToken.setToken(refreshToken.getToken());
      jsonRefreshToken.setPlayerUuid(refreshToken.getPlayer().getUuid());
      jsonRefreshToken.setExpirationDateTime(refreshToken.getExpirationDateTime());
      jsonRefreshTokens.add(jsonRefreshToken);
    }
    return jsonRefreshTokens;
  }

  public List<JsonVerificationToken> allVerificationTokens() {
    final List<JsonVerificationToken> jsonVerificationTokens = new ArrayList<>();
    final List<VerificationToken> allVerificationTokens = verificationTokenRepository.findAll();
    for (final VerificationToken verificationToken : allVerificationTokens) {
      final JsonVerificationToken jsonVerificationToken = new JsonVerificationToken();
      jsonVerificationToken.setToken(verificationToken.getToken());
      jsonVerificationToken.setPlayerUuid(verificationToken.getPlayer().getUuid());
      jsonVerificationToken.setExpirationDateTime(verificationToken.getExpirationDateTime());
      jsonVerificationTokens.add(jsonVerificationToken);
    }
    return jsonVerificationTokens;
  }

}
