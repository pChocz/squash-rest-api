package com.pj.squashrestapp.config;

import com.pj.squashrestapp.service.ExpiredTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Set of scheduled tasks to be performed on regular basis.
 *
 * NOTE: Spring cron does not follow the same format as UNIX cron expressions.
 * Proper explanation can be found below:
 *
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
 */
@Slf4j
@Component
public class ScheduledTasks {

  private static final String CRON_EVERY_FULL_MINUTE = "0 * * * * *";
  private static final String CRON_EVERY_FULL_HOUR = "0 0 * * * *";
  private static final String CRON_EVERYDAY_AT_MIDNIGHT = "0 0 0 * * *";

  @Autowired
  private ExpiredTokenService expiredTokenService;

  /**
   * Permanently removes all expired temporary tokens from database.
   * It is performed at midnight every day.
   */
  @Scheduled(cron = CRON_EVERYDAY_AT_MIDNIGHT, zone = "UTC")
  public void cronScheduledEveryday() {
    expiredTokenService.removeExpiredTokensFromDb();
  }

}
