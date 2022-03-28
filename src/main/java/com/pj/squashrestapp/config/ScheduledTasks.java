package com.pj.squashrestapp.config;

import com.pj.squashrestapp.controller.RedisCacheController;
import com.pj.squashrestapp.controller.UserAccessController;
import com.pj.squashrestapp.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

  private static final String CRON_EVERY_10_SECONDS = "*/10 * * * * *";
  private static final String CRON_EVERY_FULL_MINUTE = "0 * * * * *";
  private static final String CRON_EVERY_FULL_HOUR = "0 0 * * * *";
  private static final String CRON_EVERYDAY_AT_MIDNIGHT = "0 0 0 * * *";
  private static final String CRON_NIGHTLY_1 = "0 3 2 * * *";
  private static final String CRON_NIGHTLY_2 = "0 6 2 * * *";
  private static final String CRON_NIGHTLY_3 = "0 9 2 * * *";

  private final UserAccessController userAccessController;
  private final RedisCacheController redisCacheController;


  /**
   * Permanently removes all expired temporary tokens from database.
   */
  @Scheduled(cron = CRON_NIGHTLY_1, zone = "UTC")
  public void cronScheduledEverydayAtMidnight() {
    AuthorizationUtil.configureAuthentication("CRON", "ROLE_ADMIN");
    userAccessController.removeExpiredTokensFromDb();
    AuthorizationUtil.clearAuthentication();
  }

  /**
   * Refreshes redis cache for leagues (all rounds and all seasons scoreboards).
   */
  @Scheduled(cron = CRON_NIGHTLY_2, zone = "UTC")
  public void cronRefreshLeaguesRedisCache() {
    AuthorizationUtil.configureAuthentication("CRON", "ROLE_ADMIN");
    redisCacheController.recreateLeaguesCache();
    AuthorizationUtil.clearAuthentication();
  }

}
