package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.AdditionalMatchSimpleDto;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.scoreboard.PlayerSummary;
import com.pj.squashrestapp.dto.scoreboard.PlayersStatsScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayersScoreboardService {

  private final MatchRepository matchRepository;
  private final AdditionalMatchRepository additionalMatchRepository;
  private final PlayerRepository playerRepository;
  private final SeasonRepository seasonRepository;

  public Scoreboard buildSingle(
      final UUID leagueUuid,
      final UUID playerUuid,
      final UUID seasonUuid,
      final Integer groupNumber,
      final boolean includeAdditionalMatches) {

    final List<Match> matches =
        matchRepository.fetchForOnePlayerForLeagueForSeasonForGroupNumber(
            leagueUuid, playerUuid, seasonUuid, groupNumber);

    final List<MatchDto> roundMatchesDtos =
        matches.stream().map(MatchSimpleDto::new).collect(Collectors.toList());

    final Collection<MatchDto> allMatches = new ArrayList<>();
    allMatches.addAll(roundMatchesDtos);

    if (includeAdditionalMatches) {
      final Integer seasonNumber =
          seasonUuid == null ? null : seasonRepository.findByUuid(seasonUuid).get().getNumber();

      final List<AdditionalMatch> additionalMatches =
          additionalMatchRepository.fetchForSinglePlayerForLeagueForSeasonNumber(
              leagueUuid, playerUuid, seasonNumber);
      final List<MatchDto> additionalMatchesDtos =
          additionalMatches.stream()
              .map(AdditionalMatchSimpleDto::new)
              .collect(Collectors.toList());

      allMatches.addAll(additionalMatchesDtos);
    }

    final Scoreboard scoreboard = new Scoreboard(allMatches);
    scoreboard.makeItSinglePlayerScoreboard(playerUuid);

    return scoreboard;
  }

  public Scoreboard buildMultipleAllAgainstAll(
      final UUID leagueUuid,
      final UUID[] playersUuids,
      final UUID seasonUuid,
      final Integer groupNumber,
      final boolean includeAdditionalMatches) {

    final List<Match> roundMatches =
        matchRepository.fetchForSeveralPlayersForLeagueForSeasonForGroupNumber(
            leagueUuid, playersUuids, seasonUuid, groupNumber);
    final List<MatchDto> roundMatchesDtos =
        roundMatches.stream().map(MatchSimpleDto::new).collect(Collectors.toList());

    final Collection<MatchDto> allMatches = new ArrayList<>();
    allMatches.addAll(roundMatchesDtos);

    if (includeAdditionalMatches) {
      final Integer seasonNumber =
          seasonUuid == null ? null : seasonRepository.findByUuid(seasonUuid).get().getNumber();

      final List<AdditionalMatch> additionalMatches =
          additionalMatchRepository.fetchForSeveralPlayersForLeagueForSeasonNumber(
              leagueUuid, playersUuids, seasonNumber);
      final List<MatchDto> additionalMatchesDtos =
          additionalMatches.stream()
              .map(AdditionalMatchSimpleDto::new)
              .collect(Collectors.toList());

      allMatches.addAll(additionalMatchesDtos);
    }

    final Scoreboard scoreboard = new Scoreboard(allMatches);
    return scoreboard;
  }

  public PlayerSummary buildMeAgainstAllForAllLeagues() {
    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
    final Player player = playerRepository.findByUuid(currentPlayerUuid);
    final PlayerDto playerDto = new PlayerDto(player);
    final List<Match> roundMatches =
        matchRepository.fetchByOnePlayerAgainstAllForAllLeagues(currentPlayerUuid);
    final List<AdditionalMatch> additionalMatches =
        additionalMatchRepository.fetchAllForSinglePlayer(player);

    final List<MatchDto> roundMatchesDtos =
        roundMatches.stream().map(MatchDetailedDto::new).collect(Collectors.toList());

    final List<MatchDto> additionalMatchesDtos =
        additionalMatches.stream().map(AdditionalMatchSimpleDto::new).collect(Collectors.toList());

    final List<MatchDto> allMatchesDtos = new ArrayList<>();
    allMatchesDtos.addAll(roundMatchesDtos);
    allMatchesDtos.addAll(additionalMatchesDtos);

    final Set<UUID> leagues = new HashSet<>();
    final Set<UUID> seasons = new HashSet<>();
    final Set<UUID> rounds = new HashSet<>();

    for (final Match match : roundMatches) {
      leagues.add(match.getRoundGroup().getRound().getSeason().getLeague().getUuid());
      seasons.add(match.getRoundGroup().getRound().getSeason().getUuid());
      rounds.add(match.getRoundGroup().getRound().getUuid());
    }

    final Scoreboard scoreboard = new Scoreboard(allMatchesDtos);
    final PlayersStatsScoreboardRow scoreboardRow = scoreboard.getRowForPlayer(playerDto);

    final PlayerSummary playerSummary =
        new PlayerSummary(scoreboardRow, leagues.size(), seasons.size(), rounds.size());

    return playerSummary;
  }

  public Scoreboard buildMultipleMeAgainstAll(final UUID leagueUuid) {
    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
    final List<Match> matches =
        matchRepository.fetchByOnePlayerAgainstOthersAndLeagueId(leagueUuid, currentPlayerUuid);

    final List<MatchDto> matchesDtos =
        matches.stream().map(MatchSimpleDto::new).collect(Collectors.toList());

    final Scoreboard scoreboard = new Scoreboard(matchesDtos);
    scoreboard.removeSinglePlayer(currentPlayerUuid);
    scoreboard.reverse();

    return scoreboard;
  }
}
