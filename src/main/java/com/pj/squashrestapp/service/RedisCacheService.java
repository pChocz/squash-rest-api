package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.CacheConfiguration;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Match;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
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

  public void evictCacheForMatch(final Match matchToModify) {
    final UUID roundUuid = matchToModify.getRoundGroup().getRound().getUuid();
    final UUID seasonUuid = matchToModify.getRoundGroup().getRound().getSeason().getUuid();
    final UUID leagueUuid = matchToModify.getRoundGroup().getRound().getSeason().getLeague().getUuid();

    final UUID firstPlayerUuid = matchToModify.getFirstPlayer().getUuid();
    final UUID secondPlayerUuid = matchToModify.getSecondPlayer().getUuid();

    clearSingle(CacheConfiguration.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(CacheConfiguration.ROUND_SCOREBOARD_CACHE, roundUuid);
    clearSingle(CacheConfiguration.PLAYER_SCOREBOARD_CACHE, firstPlayerUuid);
    clearSingle(CacheConfiguration.PLAYER_SCOREBOARD_CACHE, secondPlayerUuid);
  }

  public void evictCacheForBonusPoint(final BonusPoint bonusPoint) {
    final UUID seasonUuid = bonusPoint.getSeason().getUuid();
    final UUID leagueUuid = bonusPoint.getSeason().getLeague().getUuid();

    clearSingle(CacheConfiguration.SEASON_SCOREBOARD_CACHE, seasonUuid);
  }
}
