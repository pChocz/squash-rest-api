package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
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
import com.pj.squashrestapp.repository.LeagueLogoRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class LeagueService {

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private LeagueLogoRepository leagueLogoRepository;

  @Autowired
  private SetResultRepository setResultRepository;

  @Autowired
  private XpPointsService xpPointsService;

  @Autowired
  private SeasonService seasonService;

  @Autowired
  private HallOfFameSeasonRepository hallOfFameSeasonRepository;

  public void saveLogoForLeague(final Long leagueId, final String logoBase64) {
    final Blob pictureBlob = BlobProxy.generateProxy(logoBase64.getBytes(Charset.defaultCharset()));

    final LeagueLogo leagueLogo = new LeagueLogo();
    leagueLogo.setPicture(pictureBlob);

    final League league = leagueRepository.findById(leagueId).get();
    league.setLeagueLogo(leagueLogo);
    leagueLogo.setLeague(league);

    leagueLogoRepository.save(leagueLogo);
  }

  public LeagueStatsWrapper buildStatsForLeagueId(final Long leagueId) {
    final League league = fetchEntireLeague(leagueId);
    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

    // logo
    final Blob blob = leagueLogoRepository.extractLogoBlob(leagueId);
    final String logo64encoded = extractLeagueLogo(blob);

    // per season stats
    final List<PerSeasonStats> perSeasonStatsList = buildPerSeasonStatsList(league);

    // per player scoreboards
    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList = overalXpPoints(league, xpPointsPerSplit);
    final EntireLeagueScoreboard scoreboard = new EntireLeagueScoreboard(league, playerLeagueXpOveralList);

    // build overal stats
    final OveralStats overalStats = new OveralStats(perSeasonStatsList);

    // hall of fame
    final List<HallOfFameSeason> hallOfFame = hallOfFameSeasonRepository.retrieveByLeagueId(leagueId);

    return LeagueStatsWrapper.builder()
            .leagueName(league.getName())
            .logo64encoded(logo64encoded)
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

  private String extractLeagueLogo(final Blob blob) {
    byte[] decodedBytes = new byte[0];
    if (blob != null) {
      try {
        final int length = (int) blob.length();
        final byte[] bytes = blob.getBytes(1, length);
        decodedBytes = Base64.getDecoder().decode(bytes);
        blob.free();
      } catch (final SQLException e) {
        log.error("SQL Exception when trying to encode league logo", e);
      }
    }

    return Base64.getEncoder().encodeToString(decodedBytes);
  }

  private List<PerSeasonStats> buildPerSeasonStatsList(final League league) {
    final List<PerSeasonStats> perSeasonStatsList = new ArrayList<>();

    for (final Season season : league.getSeasons()) {
      final List<MatchDto> matchesForSeason = MatchExtractorUtil.extractAllMatches(season);

      int matches = 0;
      int regularSets = 0;
      int tieBreaks = 0;
      int points = 0;

      final Multimap<Long, Long> playersAttendicesMap = LinkedHashMultimap.create();
      for (final MatchDto match : matchesForSeason) {
        matches++;
        playersAttendicesMap.put(match.getFirstPlayer().getId(), match.getRoundId());
        playersAttendicesMap.put(match.getSecondPlayer().getId(), match.getRoundId());
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

      perSeasonStatsList.add(PerSeasonStats.builder()
              .seasonNumber(season.getNumber())
              .rounds(rounds)
              .matches(matches)
              .regularSets(regularSets)
              .tieBreaks(tieBreaks)
              .tieBreaksPercents(tieBreaksPercents)
              .points(points)
              .allAttendices(playersAttendicesMap.size())
              .players(playersAttendicesMap.keySet().size())
              .playersAttendicesMap(playersAttendicesMap)
              .build());
    }

    perSeasonStatsList.sort(Comparator.comparingInt(PerSeasonStats::getSeasonNumber));
    return perSeasonStatsList;
  }

}
