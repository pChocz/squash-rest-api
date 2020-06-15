package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.controller.XpPointsService;
import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.PlayerLeagueXpOveral;
import com.pj.squashrestapp.model.dto.SetDto;
import com.pj.squashrestapp.model.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.model.dto.leaguestats.OveralStats;
import com.pj.squashrestapp.model.dto.leaguestats.PerSeasonStats;
import com.pj.squashrestapp.model.dto.scoreboard.EntireLeagueScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.repository.HallOfFameSeasonRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
  LeagueRepository leagueRepository;

  @Autowired
  SetResultRepository setResultRepository;

  @Autowired
  XpPointsService xpPointsService;

  @Autowired
  private SeasonService seasonService;

  @Autowired
  private HallOfFameSeasonRepository hallOfFameSeasonRepository;

  public LeagueStatsWrapper buildStatsForLeagueId(final Long leagueId) {
    final League league = fetchEntireLeague(leagueId);
    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

    // logo
//    final Blob blob = leagueRepository.extractLogoBlob(leagueId);
//    final String logo64encoded = extractLeagueLogo(blob);

    // per season stats
    final List<PerSeasonStats> perSeasonStatsList = buildPerSeasonStatsList(league);

    // build overal stats
    final OveralStats overalStats = new OveralStats(perSeasonStatsList);

    // per player scoreboards
    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList = overalXpPoints(league, xpPointsPerSplit);
    final EntireLeagueScoreboard scoreboard = new EntireLeagueScoreboard(league, playerLeagueXpOveralList);

    // hall of fame
    final List<HallOfFameSeason> hallOfFame = hallOfFameSeasonRepository.retrieveByLeagueId(leagueId);

    return LeagueStatsWrapper.builder()
            .leagueName(league.getName())
            .logo64encoded("dupa")
            .overalStats(overalStats)
            .perSeasonStats(perSeasonStatsList)
            .scoreboard(scoreboard)
            .hallOfFame(hallOfFame)
            .build();
  }

  public League fetchEntireLeague(final Long leagueId) {
    final List<SetResult> setResultListForLeague = setResultRepository.fetchByLeagueId(leagueId);
    return EntityGraphBuildUtil.reconstructLeague(setResultListForLeague, leagueId);
  }

//  private String extractLeagueLogo(final Blob blob) {
//    byte[] decodedBytes = new byte[0];
//    if (blob != null) {
//      try {
//        final int length = (int) blob.length();
//        final byte[] bytes = blob.getBytes(1, length);
//        decodedBytes = Base64.getDecoder().decode(bytes);
//        blob.free();
//      } catch (final SQLException e) {
//        log.error("SQL Exception when trying to encode league logo", e);
//      }
//    }
//
//    return Base64.getEncoder().encodeToString(decodedBytes);
//  }

  private List<PerSeasonStats> buildPerSeasonStatsList(final League league) {
    final List<PerSeasonStats> perSeasonStatsList = new ArrayList<>();

    for (final Season season : league.getSeasons()) {
      final List<MatchDto> matchesForSeason = MatchExtractorUtil.extractAllMatches(season);

      int matches = 0;
      int regularSets = 0;
      int tieBreaks = 0;
      int points = 0;

      for (final MatchDto match : matchesForSeason) {
        matches++;
        for (final SetDto set : match.getSets()) {
          points += set.getFirstPlayerScore();
          points += set.getSecondPlayerScore();
          if (!set.isEmpty()) {
            if (set.isTieBreak()) {
              tieBreaks++;
            } else {
              regularSets++;
            }
          }
        }
      }
      final int tieBreaksPercents = 100 * tieBreaks / matches;
      final int rounds = season.getRounds().size();
      final PerSeasonStats perSeasonStats = new PerSeasonStats(season.getNumber(), rounds, matches, regularSets, tieBreaks, tieBreaksPercents, points);
      perSeasonStatsList.add(perSeasonStats);
    }

    perSeasonStatsList.sort(Comparator.comparingInt(PerSeasonStats::getSeasonNumber));

    return perSeasonStatsList;
  }

  public List<PlayerLeagueXpOveral> overalXpPoints(final League league,
                                                   final ArrayListMultimap<String, Integer> xpPointsPerSplit) {

    final List<SeasonScoreboardDto> seasonScoreboardDtoList = league
            .getSeasons()
            .stream()
            .map(season -> seasonService.getSeasonScoreboardDto(season, xpPointsPerSplit))
            .collect(Collectors.toList());

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

}
