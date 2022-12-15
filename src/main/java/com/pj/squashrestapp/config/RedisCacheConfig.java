package com.pj.squashrestapp.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

/**
 * Configuration for Redis cache.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    public static final String LEAGUE_LOGOS_CACHE = "league_logos";
    public static final String LEAGUE_DETAILED_STATS_CACHE = "league_detailed_stats";
    public static final String LEAGUE_OVERALL_STATS_CACHE = "league_overall_stats";
    public static final String LEAGUE_ALL_ROUNDS_SCOREBOARDS = "league_all_rounds_scoreboards";
    public static final String LEAGUE_ALL_SEASONS_SCOREBOARDS = "league_all_seasons_scoreboards";
    public static final String SEASON_SCOREBOARD_CACHE = "season_scoreboard";
    public static final String ROUND_SCOREBOARD_CACHE = "round_scoreboard";
    public static final String H2H_SCOREBOARD_CACHE = "h2h_scoreboard";
    public static final String PLAYER_ALL_LEAGUES_SCOREBOARD_CACHE = "player_all_leagues_scoreboard";
    public static final String PLAYER_LEAGUE_SCOREBOARD_CACHE = "player_league_scoreboard";
    public static final String PLAYER_LEAGUE_ROUNDS_CACHE = "player_league_rounds";

    private static final Duration DEFAULT_TTL = Duration.ofHours(24);
    private static final Duration EXTENDED_TTL = Duration.ofDays(7);

    /**
     * Creates default cache configuration. It will be used for all caches except
     * ones that will be specified in the builder customizer bean below.
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration() {
        // required after upgrading Spring Boot: 2.6.5 -> 2.7.0
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(
                        LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .serializeValuesWith(
                        SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
    }

    /**
     * Configuration customizations. It is used to overwrite default configuration
     * defined in the bean above for specific cache type.
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder.enableStatistics()
                .withCacheConfiguration(
                        LEAGUE_LOGOS_CACHE,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(EXTENDED_TTL)
                                .serializeValuesWith(
                                        SerializationPair.fromSerializer(new JdkSerializationRedisSerializer())));
    }
}
