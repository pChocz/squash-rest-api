package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

  private final SetResultRepository setResultRepository;
  private final CacheManager cacheManager;

  public void clearAll() {
    for (String cacheName : cacheManager.getCacheNames()) {
      Cache cache = cacheManager.getCache(cacheName);
      if (cache != null) {
        cache.invalidate();
        log.info("Invalidated cache | {}", cacheName);
      }
    }
  }

  public void clearSingle(final String cacheName, final Object key) {
    final Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      final ValueWrapper valueWrapper = cache.get(key);
      if (valueWrapper != null) {
        cache.evictIfPresent(key);
        log.info("Evicted cache | {}::{}", cacheName, key);
      } else {
        log.info("Cache does not exist | {}::{}", cacheName, key);
      }
    }
  }

  public Set<String> getAllKeys() {
    return new LinkedHashSet<>(cacheManager.getCacheNames());
  }

  public void evictCacheForAdditionalMatch(final AdditionalMatch match) {
    final String leagueUuid = match.getLeague().getUuid().toString();

    final String p1Uuid = match.getFirstPlayer().getUuid().toString();
    final String p2Uuid = match.getSecondPlayer().getUuid().toString();

    clearSingle(RedisCacheConfig.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p1Uuid);
    clearSingle(RedisCacheConfig.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p2Uuid);
    clearSingle(RedisCacheConfig.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p1Uuid));
    clearSingle(RedisCacheConfig.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p2Uuid));
    clearSingle(RedisCacheConfig.LEAGUE_ADDITIONAL_MATCHES_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
    evictHeadToHeadStats(p1Uuid, p2Uuid);
  }

  public void evictCacheForRoundMatch(final Match match) {
    final String roundUuid = match.getRoundGroup().getRound().getUuid().toString();
    final String seasonUuid = match.getRoundGroup().getRound().getSeason().getUuid().toString();
    final String leagueUuid = match.getRoundGroup().getRound().getSeason().getLeague().getUuid().toString();

    final String p1Uuid = match.getFirstPlayer().getUuid().toString();
    final String p2Uuid = match.getSecondPlayer().getUuid().toString();

    clearSingle(RedisCacheConfig.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p1Uuid);
    clearSingle(RedisCacheConfig.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p2Uuid);
    clearSingle(RedisCacheConfig.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p1Uuid));
    clearSingle(RedisCacheConfig.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p2Uuid));
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.ROUND_SCOREBOARD_CACHE, roundUuid);
    clearSingle(RedisCacheConfig.SEASON_SCOREBOARD_CACHE, seasonUuid);
    evictHeadToHeadStats(p1Uuid, p2Uuid);
  }

  private void evictHeadToHeadStats(String p1Uuid, String p2Uuid) {
    clearSingle(RedisCacheConfig.H2H_SCOREBOARD_CACHE, String.join(",", p1Uuid, p2Uuid, "true"));
    clearSingle(RedisCacheConfig.H2H_SCOREBOARD_CACHE, String.join(",", p2Uuid, p1Uuid, "true"));
    clearSingle(RedisCacheConfig.H2H_SCOREBOARD_CACHE, String.join(",", p1Uuid, p2Uuid, "false"));
    clearSingle(RedisCacheConfig.H2H_SCOREBOARD_CACHE, String.join(",", p2Uuid, p1Uuid, "false"));
  }

  public void evictCacheForBonusPoint(final BonusPoint bonusPoint) {
    final String seasonUuid = bonusPoint.getSeason().getUuid().toString();
    final String leagueUuid = bonusPoint.getSeason().getLeague().getUuid().toString();

    clearSingle(RedisCacheConfig.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForRoundDeep(final Round round) {
    final String roundUuid = round.getUuid().toString();
    final String seasonUuid = round.getSeason().getUuid().toString();
    final String leagueUuid = round.getSeason().getLeague().getUuid().toString();

    evictCacheForRoundMatchesDeep(round);

    clearSingle(RedisCacheConfig.ROUND_SCOREBOARD_CACHE, roundUuid);
    clearSingle(RedisCacheConfig.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  private void evictCacheForSeasonMatchesDeep(Season season) {
    // for the sake of proper caching handling
    // it's required to clear cache for each match as well
    final List<SetResult> setResults = setResultRepository.fetchBySeasonUuid(season.getUuid());
    Season seasonReconstructed = EntityGraphBuildUtil.reconstructSeason(setResults, season.getId());
    if (seasonReconstructed != null) {
      for (final Round round : seasonReconstructed.getRounds()) {
        evictCacheForRoundDeep(round);
      }
    }
  }

  private void evictCacheForRoundMatchesDeep(Round round) {
    // for the sake of proper caching handling
    // it's required to clear cache for each match as well
    final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(round.getUuid());
    Round roundReconstructed = EntityGraphBuildUtil.reconstructRound(setResults, round.getId());
    if (roundReconstructed != null) {
      for (final RoundGroup roundGroup : roundReconstructed.getRoundGroups()) {
        for (final Match match : roundGroup.getMatches()) {
          evictCacheForRoundMatch(match);
        }
      }
    }
  }

  public void evictCacheForSeason(final Season season) {
    final String seasonUuid = season.getUuid().toString();
    final String leagueUuid = season.getLeague().getUuid().toString();

    evictCacheForSeasonMatchesDeep(season);

    clearSingle(RedisCacheConfig.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForLeagueLogo(final League league) {
    final String leagueUuid = league.getUuid().toString();

    clearSingle(RedisCacheConfig.LEAGUE_LOGOS_CACHE, leagueUuid);
  }
}
