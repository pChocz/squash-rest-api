package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.controller.SeasonService;
import com.pj.squashrestapp.controller.XpPointsService;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.PlayerLeagueXpOveral;
import com.pj.squashrestapp.model.dto.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.SeasonScoreboardRowDto;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class LeagueService {

  @Autowired
  SetResultRepository setResultRepository;

  @Autowired
  XpPointsService xpPointsService;

  @Autowired
  LeagueRepository leagueRepository;

  @Autowired
  private SeasonService seasonService;

  public List<PlayerLeagueXpOveral> overalXpPoints(final Long leagueId) {
    final List<SeasonScoreboardDto> seasonScoreboardDtoList = overalScoreboard(leagueId);

    final ArrayListMultimap<PlayerDto, SeasonScoreboardRowDto> playersMap = ArrayListMultimap.create();
    for (final SeasonScoreboardDto seasonScoreboardDto : seasonScoreboardDtoList) {
      for (final SeasonScoreboardRowDto seasonScoreboardRowDto : seasonScoreboardDto.getSeasonScoreboardRows()) {
        playersMap.put(seasonScoreboardRowDto.getPlayer(), seasonScoreboardRowDto);
      }
    }

    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList = playersMap
            .keySet()
            .stream()
            .map(playerDto -> new PlayerLeagueXpOveral(playersMap.get(playerDto)))
            .sorted(Comparator.comparingInt(PlayerLeagueXpOveral::getTotalPoints).reversed())
            .collect(Collectors.toList());

    return playerLeagueXpOveralList;
  }

  public List<SeasonScoreboardDto> overalScoreboard(final Long leagueId) {
    final League league = fetchEntireLeague(leagueId);
    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

    final List<SeasonScoreboardDto> seasonScoreboardDtoList = league
            .getSeasons()
            .stream()
            .map(season -> seasonService.getSeasonScoreboardDto(season, xpPointsPerSplit))
            .collect(Collectors.toList());

    return seasonScoreboardDtoList;
  }

  public League fetchEntireLeague(final Long leagueId) {
    final List<SetResult> setResultListForLeague = setResultRepository.fetchByLeagueId(leagueId);
    return EntityGraphBuildUtil.reconstructLeague(setResultListForLeague, leagueId);
  }

}
