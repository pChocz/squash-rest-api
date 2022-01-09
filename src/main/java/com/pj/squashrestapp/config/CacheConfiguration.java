package com.pj.squashrestapp.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

/** */
@Configuration
@EnableCaching
public class CacheConfiguration {

  public static final String LEAGUE_DETAILED_STATS_CACHE = "league_detailed_stats";
  public static final String LEAGUE_OVERAL_STATS_CACHE = "league_overal_stats";
  public static final String SEASON_SCOREBOARD_CACHE = "season_scoreboard";
  public static final String ROUND_SCOREBOARD_CACHE = "round_scoreboard";
  public static final String PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE = "player_all_leagues_scoreboard";
  public static final String H2H_SCOREBOARD_CACHE = "h2h_scoreboard";
  public static final String PLAYER_SINGLE_LEAGUE_SCOREBOARD_CACHE = "player_single_league_scoreboard";
  public static final String LEAGUE_ADDITIONAL_MATCHES_CACHE = "league_additional_matches";

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    return template;
  }

  @Bean
  RedisCacheConfiguration redisCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofHours(24))
        .serializeValuesWith(
            SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
  }

  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    return RedisCacheManagerBuilder::enableStatistics;
  }
}
