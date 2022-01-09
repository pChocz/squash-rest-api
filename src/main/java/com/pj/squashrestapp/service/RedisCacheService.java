package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.CacheConfiguration;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

  private final CacheManager cacheManager;
  private final RedisTemplate<String, Object> redisTemplate;

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

    clearSingle(CacheConfiguration.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p1Uuid);
    clearSingle(CacheConfiguration.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p2Uuid);
    clearSingle(CacheConfiguration.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p1Uuid));
    clearSingle(CacheConfiguration.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p2Uuid));
    clearSingle(CacheConfiguration.H2H_SCOREBOARD_CACHE, String.join(",", p1Uuid, p2Uuid, "true"));
    clearSingle(CacheConfiguration.H2H_SCOREBOARD_CACHE, String.join(",", p2Uuid, p1Uuid, "true"));
    clearSingle(CacheConfiguration.LEAGUE_ADDITIONAL_MATCHES_CACHE, leagueUuid);
    clearSingle(CacheConfiguration.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(CacheConfiguration.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForRoundMatch(final Match match) {
    final String roundUuid = match.getRoundGroup().getRound().getUuid().toString();
    final String seasonUuid = match.getRoundGroup().getRound().getSeason().getUuid().toString();
    final String leagueUuid = match.getRoundGroup().getRound().getSeason().getLeague().getUuid().toString();

    final String p1Uuid = match.getFirstPlayer().getUuid().toString();
    final String p2Uuid = match.getSecondPlayer().getUuid().toString();

    clearSingle(CacheConfiguration.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p1Uuid);
    clearSingle(CacheConfiguration.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p2Uuid);
    clearSingle(CacheConfiguration.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p1Uuid));
    clearSingle(CacheConfiguration.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p2Uuid));
    clearSingle(CacheConfiguration.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(CacheConfiguration.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
    clearSingle(CacheConfiguration.ROUND_SCOREBOARD_CACHE, roundUuid);
    clearSingle(CacheConfiguration.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(CacheConfiguration.H2H_SCOREBOARD_CACHE, String.join(",", p1Uuid, p2Uuid, "true"));
    clearSingle(CacheConfiguration.H2H_SCOREBOARD_CACHE, String.join(",", p2Uuid, p1Uuid, "true"));
    clearSingle(CacheConfiguration.H2H_SCOREBOARD_CACHE, String.join(",", p1Uuid, p2Uuid, "false"));
    clearSingle(CacheConfiguration.H2H_SCOREBOARD_CACHE, String.join(",", p2Uuid, p1Uuid, "false"));
  }

  public void evictCacheForBonusPoint(final BonusPoint bonusPoint) {
    final String seasonUuid = bonusPoint.getSeason().getUuid().toString();
    final String leagueUuid = bonusPoint.getSeason().getLeague().getUuid().toString();

    clearSingle(CacheConfiguration.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(CacheConfiguration.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(CacheConfiguration.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForRound(final Round round) {
    final String roundUuid = round.getUuid().toString();
    final String seasonUuid = round.getSeason().getUuid().toString();
    final String leagueUuid = round.getSeason().getLeague().getUuid().toString();

    clearSingle(CacheConfiguration.ROUND_SCOREBOARD_CACHE, roundUuid);
    clearSingle(CacheConfiguration.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(CacheConfiguration.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(CacheConfiguration.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForSeason(final Season season) {
    final String seasonUuid = season.getUuid().toString();
    final String leagueUuid = season.getLeague().getUuid().toString();

    clearSingle(CacheConfiguration.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(CacheConfiguration.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(CacheConfiguration.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForLeagueLogo(final League league) {
    final String leagueUuid = league.getUuid().toString();

    clearSingle(CacheConfiguration.LEAGUE_LOGOS_CACHE, leagueUuid);
  }
}
