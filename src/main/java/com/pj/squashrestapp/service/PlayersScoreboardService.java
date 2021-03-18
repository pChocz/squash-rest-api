package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.dto.match.MatchDto;
import com.pj.squashrestapp.model.dto.scoreboard.PlayerSummary;
import com.pj.squashrestapp.model.dto.scoreboard.PlayersStatsScoreboardRow;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayersScoreboardService {

  private final MatchRepository matchRepository;
  private final PlayerRepository playerRepository;

  public Scoreboard buildSingle(final UUID leagueUuid, final UUID playerUuid,
                                final UUID seasonUuid, final Integer groupNumber) {

    final List<Match> matches = matchRepository.fetchForOnePlayerForLeagueForSeasonForGroupNumber(leagueUuid, playerUuid, seasonUuid, groupNumber);

    final List<MatchDto> matchesDtos = matches
            .stream()
            .map(MatchDetailedDto::new)
            .collect(Collectors.toList());

    final Scoreboard scoreboard = new Scoreboard(matchesDtos);
    scoreboard.makeItSinglePlayerScoreboard(playerUuid);

    return scoreboard;
  }

  public Scoreboard buildMultipleAllAgainstAll(final UUID leagueUuid, final UUID[] playersUuids,
                                               final UUID seasonUuid, final Integer groupNumber) {

    final List<Match> matches = matchRepository.fetchForSeveralPlayersForLeagueForSeasonForGroupNumber(leagueUuid, playersUuids, seasonUuid, groupNumber);

    final List<MatchDto> matchesDtos = matches
            .stream()
            .map(MatchDetailedDto::new)
            .collect(Collectors.toList());

    final Scoreboard scoreboard = new Scoreboard(matchesDtos);

    return scoreboard;
  }

  public PlayerSummary buildMeAgainstAllForAllLeagues() {
    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
    final Player player = playerRepository.findByUuid(currentPlayerUuid);
    final PlayerDto playerDto = new PlayerDto(player);
    final List<Match> matches = matchRepository.fetchByOnePlayerAgainstAllForAllLeagues(currentPlayerUuid);

    final List<MatchDto> matchesDtos = matches
            .stream()
            .map(MatchDetailedDto::new)
            .collect(Collectors.toList());

    final Set<UUID> leagues = new HashSet<>();
    final Set<UUID> seasons = new HashSet<>();
    final Set<UUID> rounds = new HashSet<>();

    for (final Match match : matches) {
      leagues.add(match.getRoundGroup().getRound().getSeason().getLeague().getUuid());
      seasons.add(match.getRoundGroup().getRound().getSeason().getUuid());
      rounds.add(match.getRoundGroup().getRound().getUuid());
    }

    final Scoreboard scoreboard = new Scoreboard(matchesDtos);
    final PlayersStatsScoreboardRow scoreboardRow = scoreboard.getRowForPlayer(playerDto);

    final PlayerSummary playerSummary = new PlayerSummary(scoreboardRow, leagues.size(), seasons.size(), rounds.size());

    return playerSummary;
  }

  public Scoreboard buildMultipleMeAgainstAll(final UUID leagueUuid) {
    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
    final List<Match> matches = matchRepository.fetchByOnePlayerAgainstOthersAndLeagueId(leagueUuid, currentPlayerUuid);

    final List<MatchDto> matchesDtos = matches
            .stream()
            .map(MatchDetailedDto::new)
            .collect(Collectors.toList());

    final Scoreboard scoreboard = new Scoreboard(matchesDtos);
    scoreboard.removeSinglePlayer(currentPlayerUuid);
    scoreboard.reverse();

    return scoreboard;
  }

}
