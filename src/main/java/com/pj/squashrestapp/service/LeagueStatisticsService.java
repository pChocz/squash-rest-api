package com.pj.squashrestapp.service;

import com.google.common.collect.Multimap;
import com.pj.squashrestapp.util.MatchUtil;
import com.pj.squashrestapp.model.dto.HallOfFameSeasonDto;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.Scoreboard;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import com.pj.squashrestapp.repository.HallOfFameSeasonRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class LeagueStatisticsService {

  @Getter
  @Autowired
  private MatchRepository matchRepository;

  @Getter
  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private HallOfFameSeasonRepository hallOfFameSeasonRepository;

  public LeagueStatsWrapper buildStatsForLeagueId(final Long id) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveByLeagueIdFinishedRoundsOnly(id);
    final Multimap<Integer, MatchDto> matchesPerSeason = MatchUtil.rebuildRoundMatchesPerSeasonNumber(sets);

    // logo
    final String logo64encoded = extractLeagueLogo(id);

    // build overal stats
    final OveralStats overalStats = new OveralStats(matchesPerSeason);

    // per player scoreboards
    final Scoreboard scoreboard = new Scoreboard(matchesPerSeason.values());

    // hall of fame
    final List<HallOfFameSeasonDto> hallOfFameDto = extractHallOfFameSeason(id);

    return new LeagueStatsWrapper(logo64encoded, overalStats, scoreboard, hallOfFameDto);
  }

  private String extractLeagueLogo(final Long id) {
    final Blob blob = leagueRepository.retrieveLogoForLeagueId(id);
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

  private List<HallOfFameSeasonDto> extractHallOfFameSeason(final Long id) {
    return hallOfFameSeasonRepository
            .retrieveByLeagueId(id)
            .stream()
            .map(HallOfFameSeasonDto::new)
            .collect(Collectors.toList());
  }

}
