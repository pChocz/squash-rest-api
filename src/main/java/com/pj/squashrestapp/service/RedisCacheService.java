package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
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
  private final RoundRepository roundRepository;
  private final SeasonRepository seasonRepository;
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

  public void evictCacheForSeasonMatches(UUID seasonUuid) {
    final List<SetResult> setResults = setResultRepository.fetchBySeasonUuid(seasonUuid);
    final Long seasonId = seasonRepository.findIdByUuid(seasonUuid);

    Season season = EntityGraphBuildUtil.reconstructSeason(setResults, seasonId);
    if (season == null) {
      season =
          seasonRepository
              .findSeasonByUuid(seasonUuid)
              .orElseThrow(() -> new NoSuchElementException("Season does not exist!"));
    }
    final League league = season.getLeague();

    final Set<UUID> allPlayersUuids = new HashSet<>();
    for (final Round round : season.getRounds()) {
      List<UUID[]> playersUuidsPerGroup = round.extractPlayersUuidsPerGroup();
      for (final UUID[] uuids : playersUuidsPerGroup) {
        allPlayersUuids.addAll(Arrays.stream(uuids).toList());
      }
      evictCacheForPlayersHeadToHead(playersUuidsPerGroup);
    }
    evictCacheForEachPlayers(allPlayersUuids, league.getUuid());
  }

  private void evictCacheForEachPlayers(Set<UUID> playersUuids, UUID leagueUuid) {
    for (final UUID playerUuid : playersUuids) {
      clearSingle(RedisCacheConfig.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, playerUuid);
      clearSingle(
          RedisCacheConfig.PLAYER_LEAGUE_SCOREBOARD_CACHE,
          String.join(",", leagueUuid.toString(), playerUuid.toString()));
    }
  }

  public void evictCacheForRoundMatches(UUID roundUuid) {
    final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(roundUuid);
    final Long roundId = roundRepository.findIdByUuid(roundUuid);

    Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
    if (round == null) {
      round = roundRepository.findByUuidWithSeasonAndLeague(roundUuid);
    }

    final League league = round.getSeason().getLeague();

    final Set<UUID> allPlayersUuids = new HashSet<>();
    final List<UUID[]> playersUuidsPerGroup = round.extractPlayersUuidsPerGroup();
    for (final UUID[] uuids : playersUuidsPerGroup) {
      allPlayersUuids.addAll(Arrays.stream(uuids).toList());
    }
    evictCacheForEachPlayers(allPlayersUuids, league.getUuid());
    evictCacheForPlayersHeadToHead(playersUuidsPerGroup);
  }

  public void evictCacheForPlayersHeadToHead(List<UUID[]> playersUuids) {
    for (final UUID[] group : playersUuids) {
      List<UUID> sortedUuids = Stream.of(group).sorted().toList();
      for (int i = 0; i < sortedUuids.size(); i++) {
        final String p1Uuid = sortedUuids.get(i).toString();
        for (int j = i + 1; j < sortedUuids.size(); j++) {
          final String p2Uuid = sortedUuids.get(j).toString();
          evictHeadToHeadStats(p1Uuid, p2Uuid);
        }
      }
    }
  }

  public void evictCacheForAdditionalMatch(final AdditionalMatch match) {
    final String leagueUuid = match.getLeague().getUuid().toString();
    final String p1Uuid = match.getFirstPlayer().getUuid().toString();
    final String p2Uuid = match.getSecondPlayer().getUuid().toString();

    clearSingle(RedisCacheConfig.LEAGUE_ADDITIONAL_MATCHES_CACHE, leagueUuid);

    evictMatchRelatedCache(leagueUuid, p1Uuid, p2Uuid);
    evictHeadToHeadStats(p1Uuid, p2Uuid);
  }

  public void evictCacheForRoundMatch(final Match match) {
    final String roundUuid = match.getRoundGroup().getRound().getUuid().toString();
    final String seasonUuid = match.getRoundGroup().getRound().getSeason().getUuid().toString();
    final String leagueUuid = match.getRoundGroup().getRound().getSeason().getLeague().getUuid().toString();
    final String p1Uuid = match.getFirstPlayer().getUuid().toString();
    final String p2Uuid = match.getSecondPlayer().getUuid().toString();

    clearSingle(RedisCacheConfig.ROUND_SCOREBOARD_CACHE, roundUuid);
    clearSingle(RedisCacheConfig.SEASON_SCOREBOARD_CACHE, seasonUuid);

    evictMatchRelatedCache(leagueUuid, p1Uuid, p2Uuid);
    evictHeadToHeadStats(p1Uuid, p2Uuid);
  }

  private void evictMatchRelatedCache(String leagueUuid, String p1Uuid, String p2Uuid) {
    clearSingle(RedisCacheConfig.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p1Uuid);
    clearSingle(RedisCacheConfig.PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE, p2Uuid);
    clearSingle(RedisCacheConfig.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p1Uuid));
    clearSingle(RedisCacheConfig.PLAYER_LEAGUE_SCOREBOARD_CACHE, String.join(",", leagueUuid, p2Uuid));
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  private void evictHeadToHeadStats(String p1Uuid, String p2Uuid) {
    clearSingle(RedisCacheConfig.H2H_SCOREBOARD_CACHE, String.join(",", p1Uuid, p2Uuid, "true"));
    clearSingle(RedisCacheConfig.H2H_SCOREBOARD_CACHE, String.join(",", p1Uuid, p2Uuid, "false"));
  }

  public void evictCacheForBonusPoint(final BonusPoint bonusPoint) {
    final String seasonUuid = bonusPoint.getSeason().getUuid().toString();
    final String leagueUuid = bonusPoint.getSeason().getLeague().getUuid().toString();

    clearSingle(RedisCacheConfig.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForRound(final UUID roundUuid) {
    final Round round = roundRepository.findByUuidWithSeasonAndLeague(roundUuid);
    final String seasonUuid = round.getSeason().getUuid().toString();
    final String leagueUuid = round.getSeason().getLeague().getUuid().toString();

    clearSingle(RedisCacheConfig.ROUND_SCOREBOARD_CACHE, roundUuid);
    clearSingle(RedisCacheConfig.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForSeason(final UUID seasonUuid) {
    final Season season = seasonRepository.findByUuidWithLeague(seasonUuid);
    final String leagueUuid = season.getLeague().getUuid().toString();

    clearSingle(RedisCacheConfig.SEASON_SCOREBOARD_CACHE, seasonUuid);
    clearSingle(RedisCacheConfig.LEAGUE_DETAILED_STATS_CACHE, leagueUuid);
    clearSingle(RedisCacheConfig.LEAGUE_OVERALL_STATS_CACHE, leagueUuid);
  }

  public void evictCacheForLeagueLogo(final UUID leagueUuid) {
    clearSingle(RedisCacheConfig.LEAGUE_LOGOS_CACHE, leagueUuid);
  }
}
