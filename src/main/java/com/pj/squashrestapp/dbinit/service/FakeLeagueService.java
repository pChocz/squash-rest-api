package com.pj.squashrestapp.dbinit.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.dbinit.fake.FakeLeagueHallOfFame;
import com.pj.squashrestapp.dbinit.fake.FakePlayersCreator;
import com.pj.squashrestapp.dbinit.fake.FakePlayersRoleAssigner;
import com.pj.squashrestapp.dbinit.fake.FakeSeason;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.HallOfFameSeasonRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.service.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.service.SeasonService;
import com.pj.squashrestapp.service.XpPointsService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class FakeLeagueService {

  private static final int WEEKS_BETWEEN_SEASONS_STARTS = 12;
  private static final int NUMBER_OF_ROUNDS_COMPLETE = 10;


  @Autowired
  private AuthorityRepository authorityRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private HallOfFameSeasonRepository hallOfFameSeasonRepository;

  @Autowired
  private XpPointsService xpPointsService;

  @Autowired
  private SeasonService seasonService;


  public void buildLeague(final String leagueName,
                          final int numberOfCompletedSeasons,
                          final int numberOfRoundsInLastSeason,
                          final int numberOfAllPlayers,
                          final int minNumberOfAttendingPlayers,
                          final int maxNumberOfAttendingPlayers,
                          final LocalDate startDate,
                          final byte[] logoBytes) throws IOException {

    log.info("-- START --");
    final long veryStartTime = System.nanoTime();

    log.info("Creating new League and assigning roles and logo...");
    long startTime = System.nanoTime();

    final League league = new League(leagueName);

    final LeagueLogo leagueLogo = new LeagueLogo();
    leagueLogo.setPicture(logoBytes);
    league.setLeagueLogo(leagueLogo);

    final RoleForLeague moderatorRole = new RoleForLeague(LeagueRole.MODERATOR);
    final RoleForLeague playerRole = new RoleForLeague(LeagueRole.PLAYER);
    league.addRoleForLeague(moderatorRole);
    league.addRoleForLeague(playerRole);
    final Authority userAuthority = authorityRepository.findByType(AuthorityType.ROLE_USER);
    TimeLogUtil.logFinish(startTime);


    log.info("Creating {} players (including password hashing) and assigning roles/authorities...", numberOfAllPlayers);
    startTime = System.nanoTime();
    final List<Player> players = FakePlayersCreator.create(numberOfAllPlayers);
    FakePlayersRoleAssigner.assignRolesAndAuthorities(players, moderatorRole, playerRole, userAuthority);
    TimeLogUtil.logFinish(startTime);


    log.info("Creating {} complete seasons (incl. Bonus Points)...", numberOfCompletedSeasons);
    startTime = System.nanoTime();
    LocalDate seasonStartDate = startDate;
    for (int seasonNumber = 1; seasonNumber <= numberOfCompletedSeasons; seasonNumber++) {
      final Season season = FakeSeason.create(
              seasonNumber,
              seasonStartDate,
              NUMBER_OF_ROUNDS_COMPLETE,
              players,
              minNumberOfAttendingPlayers,
              maxNumberOfAttendingPlayers);
      league.addSeason(season);

      seasonStartDate = seasonStartDate.plusWeeks(WEEKS_BETWEEN_SEASONS_STARTS);
    }
    if (numberOfRoundsInLastSeason > 0) {
      final Season season = FakeSeason.create(
              numberOfCompletedSeasons + 1,
              seasonStartDate,
              numberOfRoundsInLastSeason,
              players,
              minNumberOfAttendingPlayers,
              maxNumberOfAttendingPlayers);
      league.addSeason(season);
    }
    TimeLogUtil.logFinish(startTime);


    log.info("Persisting {} items (seasons/rounds/roundGroups/matches/sets + players) to PostreSQL DB...", extractNumberOfEntities(league, players));
    startTime = System.nanoTime();
    playerRepository.saveAll(players);
    leagueRepository.save(league);
    TimeLogUtil.logFinish(startTime);


    log.info("Calculating scoreboards to fill the Hall of Fame...");
    startTime = System.nanoTime();
    // only after persisting the league we can calculate the scoreboard (as some of the logic is based on the Id)
    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();
    for (final Season season : league.getSeasons()) {
      if (season.getRounds().size() == 10) {
        final List<BonusPoint> bonusPoints = new ArrayList<>(season.getBonusPoints());
        final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason = new BonusPointsAggregatedForSeason(season.getId(), bonusPoints);
        final SeasonScoreboardDto seasonScoreboardDto = seasonService.getSeasonScoreboardDto(season, xpPointsPerSplit, bonusPointsAggregatedForSeason);

        for (final SeasonScoreboardRowDto seasonScoreboardRowDto : seasonScoreboardDto.getSeasonScoreboardRows()) {
          seasonScoreboardRowDto.calculateFinishedRow(seasonScoreboardDto.getFinishedRounds(), seasonScoreboardDto.getCountedRounds());
        }
        seasonScoreboardDto.sortByCountedPoints();

        final HallOfFameSeason hallOfFameForSeason = FakeLeagueHallOfFame.create(seasonScoreboardDto);
        league.addHallOfFameSeason(hallOfFameForSeason);
      }
    }
    hallOfFameSeasonRepository.saveAll(league.getHallOfFameSeasons());
    TimeLogUtil.logFinish(startTime);


    log.info("-- FINISHED --");
    TimeLogUtil.logFinish(veryStartTime);
    log.info(extractLeagueDetails(league));
  }

  private int extractNumberOfEntities(final League league, final List<Player> players) {
    return extractNumberOfSeasons(league)
           + extractNumberOfRounds(league)
           + extractNumberOfRoundGroups(league)
           + extractNumberOfMatches(league)
           + extractNumberOfSets(league)
           + players.size();
  }

  private String extractLeagueDetails(final League league) {
    final int numberOfSeasons = extractNumberOfSeasons(league);
    final int numberOfRounds = extractNumberOfRounds(league);
    final int numberOfMatches = extractNumberOfMatches(league);

    return new StringBuilder()
            .append("\n\t Details of league " + league.getName() + "\n")
            .append("\t\t League ID:\t " + league.getId() + "\n")
            .append("\t\t Seasons:\t " + numberOfSeasons + "\n")
            .append("\t\t Rounds:\t " + numberOfRounds + "\n")
            .append("\t\t Matches:\t " + numberOfMatches + "\n")
            .toString();
  }

  private int extractNumberOfSeasons(final League league) {
    return league
            .getSeasons()
            .size();
  }

  private int extractNumberOfRounds(final League league) {
    return league
            .getSeasons()
            .stream()
            .mapToInt(season -> season
                    .getRounds()
                    .size())
            .sum();
  }

  private int extractNumberOfRoundGroups(final League league) {
    return league
            .getSeasons()
            .stream()
            .mapToInt(season -> season
                    .getRounds()
                    .stream()
                    .mapToInt(round -> round
                            .getRoundGroups()
                            .size())
                    .sum())
            .sum();
  }

  private int extractNumberOfMatches(final League league) {
    return league
            .getSeasons()
            .stream()
            .mapToInt(season -> season
                    .getRounds()
                    .stream()
                    .mapToInt(round -> round
                            .getRoundGroups()
                            .stream()
                            .mapToInt(roundGroup -> roundGroup
                                    .getMatches()
                                    .size())
                            .sum())
                    .sum())
            .sum();
  }

  private int extractNumberOfSets(final League league) {
    return league
            .getSeasons()
            .stream()
            .mapToInt(season -> season
                    .getRounds()
                    .stream()
                    .mapToInt(round -> round
                            .getRoundGroups()
                            .stream()
                            .mapToInt(roundGroup -> roundGroup
                                    .getMatches()
                                    .stream()
                                    .mapToInt(match -> match
                                            .getSetResults()
                                            .size())
                                    .sum())
                            .sum())
                    .sum())
            .sum();
  }

}
