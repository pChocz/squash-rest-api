package com.pj.squashrestapp.config;

import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.dto.leaguestats.OveralStats;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.service.RedisCacheService;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.TokenRemovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private static final String CRON_EVERYDAY_AT_TWO = "0 0 2 * * *";

  private final TokenRemovalService tokenRemovalService;
  private final RedisCacheService redisCacheService;
  private final ScoreboardService scoreboardService;
  private final LeagueService leagueService;

  /**
   * Permanently removes all expired temporary tokens from database. It is performed at midnight
   * every day.
   */
  @Scheduled(cron = CRON_EVERYDAY_AT_MIDNIGHT, zone = "UTC")
  public void cronScheduledEverydayAtMidnight() {
    tokenRemovalService.removeExpiredTokensFromDb();
  }

  /**
   * Refreshes redis cache for leagues (all rounds and all seasons scoreboards)
   */
  @Scheduled(cron = CRON_EVERYDAY_AT_TWO, zone = "UTC")
  public void refreshLeaguesRedisCache() {
    log.info("Invalidating REDIS cache and creating new");
    redisCacheService.clearAll();
    List<LeagueDto> allLeagues = leagueService.buildGeneralInfoForAllLeagues();
    for (final LeagueDto leagueDto : allLeagues) {
      final UUID leagueUuid = leagueDto.getLeagueUuid();
      final List<RoundScoreboard> roundScoreboards = scoreboardService.allRoundsScoreboards(leagueUuid);
      final List<SeasonScoreboardDto> seasonScoreboards = scoreboardService.allSeasonsScoreboards(leagueUuid);
      final OveralStats leagueOveralStats = leagueService.buildOveralStatsForLeagueUuid(leagueUuid);
      final LeagueStatsWrapper leagueStats = leagueService.buildStatsForLeagueUuid(leagueUuid);
    }
    log.info("Recreated REDIS cache for all leagues");
  }

}
