package com.pj.squashrestapp.config.cron;

import com.pj.squashrestapp.controller.RedisCacheController;
import com.pj.squashrestapp.controller.UserAccessController;
import com.pj.squashrestapp.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Set of scheduled tasks to be performed on regular basis.
 *
 * <p>NOTE: Spring cron does not follow the same format as UNIX cron expressions. Proper explanation
 * can be found below:
 *
 * <pre>
 * 1 2 3 4 5 6
 * * * * * * *
 * - - - - - -
 * | | | | | |
 * | | | | | ------- Day of week    (MON - SUN)
 * | | | | --------- Month          (1 - 12)
 * | | | ----------- Day of month   (1 - 31)
 * | | ------------- Hour           (0 - 23)
 * | --------------- Minute         (0 - 59)
 * ----------------- Seconds        (0 - 59)
 *
 * Examples:
 * - every 10 seconds   ->  * /10 * * * * *  (no space)
 * - every minute       ->  0 * * * * *
 *
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CronActions {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String CRON_USER = "CRON";
    private static final String UTC_ZONE = "UTC";

    private final UserAccessController userAccessController;
    private final RedisCacheController redisCacheController;

    /**
     * Permanently removes all expired temporary tokens from database.
     * 01:57:00 UTC
     */
    @Scheduled(cron = "0 57 1 * * *", zone = UTC_ZONE)
    @SchedulerLock(name = "REMOVE_EXPIRED_TOKENS_LOCK")
    public void removeExpiredTokensFrom() {
        AuthorizationUtil.configureAuthentication(CRON_USER, ROLE_ADMIN);
        userAccessController.removeExpiredTokensFromDb();
        AuthorizationUtil.clearAuthentication();
    }

    /**
     * Clears whole redis cache.
     * 02:02:00 UTC
     */
    @Scheduled(cron = "0 2 2 * * *", zone = UTC_ZONE)
    @SchedulerLock(name = "REDIS_CLEAR_LOCK")
    public void clearRedisCache() {
        AuthorizationUtil.configureAuthentication(CRON_USER, ROLE_ADMIN);
        redisCacheController.clearWholeCache();
        AuthorizationUtil.clearAuthentication();
    }

    /**
     * Refreshes redis cache for leagues (all rounds and all seasons scoreboards).
     * 02:07:00 UTC
     */
    @Scheduled(cron = "0 7 2 * * *", zone = UTC_ZONE)
    @SchedulerLock(name = "LEAGUES_ROUNDS_SEASONS_SCOREBOARDS_LOCK")
    public void refreshLeaguesRoundsAndSeasonsRedisCache() {
        AuthorizationUtil.configureAuthentication(CRON_USER, ROLE_ADMIN);
        final List<UUID> uuids = List.of(
                UUID.fromString("8e1b0f1a-931f-48aa-a187-6f0b74c4a2ab"),
                UUID.fromString("dd37d9d2-99d5-41c8-bf5b-350b77294a73"),
                UUID.fromString("ce4b762d-c18b-41d1-992c-f98a4a463c72"));
        redisCacheController.recreateGivenLeaguesSmallScoreboardsCache(uuids);
        AuthorizationUtil.clearAuthentication();
    }

    /**
     * Refreshes redis cache for leagues (all rounds and all seasons scoreboards).
     * 02:17:00 UTC
     */
    @Scheduled(cron = "0 17 2 * * *", zone = UTC_ZONE)
    @SchedulerLock(name = "LEAGUES_OVERALL_SCOREBOARDS_LOCK")
    public void refreshLeaguesAllOverallScoreboardsRedisCache() {
        AuthorizationUtil.configureAuthentication(CRON_USER, ROLE_ADMIN);
        redisCacheController.recreateAllLeaguesBigScoreboardsCache();
        AuthorizationUtil.clearAuthentication();
    }
}
