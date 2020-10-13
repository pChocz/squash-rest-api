package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.dto.match.MatchDto;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

  public Scoreboard buildMultipleMeAgainstAll(final UUID leagueUuid, final UUID[] playersUuids) {
    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
    final List<Match> matches = matchRepository.fetchByOnePlayerAgainstOthersAndLeagueId(leagueUuid, currentPlayerUuid, playersUuids);

    final List<MatchDto> matchesDtos = matches
            .stream()
            .map(MatchDetailedDto::new)
            .collect(Collectors.toList());

    final Scoreboard scoreboard = new Scoreboard(matchesDtos);
    scoreboard.removeSinglePlayer(currentPlayerUuid);

    return scoreboard;
  }

}
