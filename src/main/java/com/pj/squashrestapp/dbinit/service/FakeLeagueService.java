package com.pj.squashrestapp.dbinit.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.dbinit.fake.FakeLeagueHallOfFame;
import com.pj.squashrestapp.dbinit.fake.FakePlayersCreator;
import com.pj.squashrestapp.dbinit.fake.FakePlayersRoleAssigner;
import com.pj.squashrestapp.dbinit.jsondto.JsonFakeLeagueParams;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.dto.LostBallsAggregatedForSeason;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.enums.AuthorityType;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.enums.LeagueRole;
import com.pj.squashrestapp.model.LostBall;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.enums.SetWinningType;
import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.TrophiesForLeagueRepository;
import com.pj.squashrestapp.service.SeasonService;
import com.pj.squashrestapp.service.XpPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.v;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class FakeLeagueService {

    private final XpPointsService xpPointsService;
    private final SeasonService seasonService;
    private final FakeSeasonService fakeSeasonService;

    private final AuthorityRepository authorityRepository;
    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;
    private final TrophiesForLeagueRepository trophiesForLeagueRepository;
    private final PasswordEncoder passwordEncoder;

    public void buildLeagues(final List<JsonFakeLeagueParams> jsonFakeLeagueParamsList) throws IOException {
        log.info("-- START building of {} fake leagues --", jsonFakeLeagueParamsList.size());

        for (final JsonFakeLeagueParams jsonFakeLeagueParams : jsonFakeLeagueParamsList) {
            buildLeague(jsonFakeLeagueParams);
        }

        log.info("-- FINISHED building of {} fake leagues --", jsonFakeLeagueParamsList.size());
    }

    public void buildLeague(final JsonFakeLeagueParams jsonFakeLeagueParams) throws IOException {
        final String leagueName = jsonFakeLeagueParams.getLeagueName();
        final int numberOfCompletedSeasons = jsonFakeLeagueParams.getNumberOfCompletedSeasons();
        final int numberOfRoundsInLastSeason = jsonFakeLeagueParams.getNumberOfRoundsInLastSeason();
        final int numberOfAllPlayers = jsonFakeLeagueParams.getNumberOfAllPlayers();
        final int minNumberOfAttendingPlayers = jsonFakeLeagueParams.getMinNumberOfAttendingPlayers();
        final int maxNumberOfAttendingPlayers = jsonFakeLeagueParams.getMaxNumberOfAttendingPlayers();
        final String xpPointsType = jsonFakeLeagueParams.getXpPointsType();
        final LocalDate startDate = jsonFakeLeagueParams.getStartDate();

        final int numberOfRoundsPerSeason = jsonFakeLeagueParams.getNumberOfRoundsPerSeason();
        final int numberOfRoundsToBeDeducted = jsonFakeLeagueParams.getNumberOfRoundsToBeDeducted();
        final MatchFormatType matchFormatType = jsonFakeLeagueParams.getMatchFormatType();
        final SetWinningType regularSetWinningType = jsonFakeLeagueParams.getRegularSetWinningType();
        final int regularSetWinningPoints = jsonFakeLeagueParams.getRegularSetWinningPoints();
        final SetWinningType tiebreakWinningType = jsonFakeLeagueParams.getTiebreakWinningType();
        final int tiebreakWinningPoints = jsonFakeLeagueParams.getTiebreakWinningPoints();
        final String when = jsonFakeLeagueParams.getWhen();
        final String where = jsonFakeLeagueParams.getWhere();

        final String logoBase64 = jsonFakeLeagueParams.getLogoBase64();
        final byte[] logoBytes = Base64.getDecoder().decode(logoBase64);

        log.info("Creating new League and assigning roles and logo...");

        final League league = new League(leagueName);
        league.setNumberOfRoundsPerSeason(numberOfRoundsPerSeason);
        league.setRoundsToBeDeducted(numberOfRoundsToBeDeducted);
        league.setMatchFormatType(matchFormatType);
        league.setRegularSetWinningType(regularSetWinningType);
        league.setRegularSetWinningPoints(regularSetWinningPoints);
        league.setTiebreakWinningType(tiebreakWinningType);
        league.setTiebreakWinningPoints(tiebreakWinningPoints);
        league.setDateOfCreation(startDate.atTime(7, 0));
        league.setTime(when);
        league.setLocation(where);

        final LeagueLogo leagueLogo = new LeagueLogo();
        leagueLogo.setPicture(logoBytes);
        league.setLeagueLogo(leagueLogo);

        final RoleForLeague ownerRole = new RoleForLeague(LeagueRole.OWNER);
        final RoleForLeague moderatorRole = new RoleForLeague(LeagueRole.MODERATOR);
        final RoleForLeague playerRole = new RoleForLeague(LeagueRole.PLAYER);
        league.addRoleForLeague(ownerRole);
        league.addRoleForLeague(moderatorRole);
        league.addRoleForLeague(playerRole);
        final Authority userAuthority = authorityRepository.findByType(AuthorityType.ROLE_USER);

        log.info(
                "Creating {} players (including password hashing) and assigning roles/authorities...",
                numberOfAllPlayers);
        final List<Player> players = FakePlayersCreator.create(numberOfAllPlayers, passwordEncoder);
        FakePlayersRoleAssigner.assignRolesAndAuthorities(players, ownerRole, moderatorRole, playerRole, userAuthority);

        log.info("Creating {} complete seasons (incl. Bonus Points)...", numberOfCompletedSeasons);
        LocalDate seasonStartDate = startDate;
        for (int seasonNumber = 1; seasonNumber <= numberOfCompletedSeasons; seasonNumber++) {
            final Season season = fakeSeasonService.create(
                    league,
                    seasonNumber,
                    seasonStartDate,
                    numberOfRoundsPerSeason,
                    players,
                    minNumberOfAttendingPlayers,
                    maxNumberOfAttendingPlayers,
                    xpPointsType);
            league.addSeason(season);

            seasonStartDate = seasonStartDate.plusWeeks(numberOfRoundsPerSeason + 2);
        }
        if (numberOfRoundsInLastSeason > 0) {
            final Season season = fakeSeasonService.create(
                    league,
                    numberOfCompletedSeasons + 1,
                    seasonStartDate,
                    numberOfRoundsInLastSeason,
                    players,
                    minNumberOfAttendingPlayers,
                    maxNumberOfAttendingPlayers,
                    xpPointsType);
            league.addSeason(season);
        }

        log.info(
                "Persisting {} items (seasons/rounds/roundGroups/matches/sets + players) to PostreSQL DB...",
                extractNumberOfEntities(league, players));
        playerRepository.saveAll(players);
        authorityRepository.save(userAuthority);
        leagueRepository.save(league);

        log.info("Calculating scoreboards to fill the Hall of Fame...");

        // only after persisting the league we can calculate the scoreboard (as some of the logic is
        // based on the Id)
        final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();
        for (final Season season : league.getSeasons()) {
            if (season.getRounds().size() == league.getNumberOfRoundsPerSeason()) {
                final List<BonusPoint> bonusPoints = new ArrayList<>(season.getBonusPoints());
                final List<LostBall> lostBalls = new ArrayList<>(season.getLostBalls());
                final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason =
                        new BonusPointsAggregatedForSeason(season.getUuid(), bonusPoints);
                final LostBallsAggregatedForSeason lostBallsAggregatedForSeason =
                        new LostBallsAggregatedForSeason(season.getUuid(), lostBalls);
                final SeasonScoreboardDto seasonScoreboardDto = seasonService.getSeasonScoreboardDto(
                        season, xpPointsPerSplit, bonusPointsAggregatedForSeason, lostBallsAggregatedForSeason);

                for (final SeasonScoreboardRowDto seasonScoreboardRowDto :
                        seasonScoreboardDto.getSeasonScoreboardRows()) {
                    seasonScoreboardRowDto.calculateFinishedRow(seasonScoreboardDto.getCountedRounds());
                }
                seasonScoreboardDto.sortByCountedPoints();

                final List<Player> allPlayers = playerRepository.findAll();
                final List<TrophyForLeague> trophiesForLeague =
                        FakeLeagueHallOfFame.create(seasonScoreboardDto, allPlayers);
                for (final TrophyForLeague trophyForLeague : trophiesForLeague) {
                    league.addTrophyForLeague(trophyForLeague);
                }
            }
        }
        trophiesForLeagueRepository.saveAll(league.getTrophiesForLeague());

        log.info("Fake league has been created",
                v("league", league.getName()),
                v("seasons", extractNumberOfSeasons(league)),
                v("rounds", extractNumberOfRounds(league)),
                v("matches", extractNumberOfMatches(league))
        );
    }

    private int extractNumberOfEntities(final League league, final List<Player> players) {
        return extractNumberOfSeasons(league)
                + extractNumberOfRounds(league)
                + extractNumberOfRoundGroups(league)
                + extractNumberOfMatches(league)
                + extractNumberOfSets(league)
                + players.size();
    }

    private int extractNumberOfSeasons(final League league) {
        return league.getSeasons().size();
    }

    private int extractNumberOfRounds(final League league) {
        return league.getSeasons().stream()
                .mapToInt(season -> season.getRounds().size())
                .sum();
    }

    private int extractNumberOfRoundGroups(final League league) {
        return league.getSeasons().stream()
                .mapToInt(season -> season.getRounds().stream()
                        .mapToInt(round -> round.getRoundGroups().size())
                        .sum())
                .sum();
    }

    private int extractNumberOfMatches(final League league) {
        return league.getSeasons().stream()
                .mapToInt(season -> season.getRounds().stream()
                        .mapToInt(round -> round.getRoundGroups().stream()
                                .mapToInt(roundGroup -> roundGroup.getMatches().size())
                                .sum())
                        .sum())
                .sum();
    }

    private int extractNumberOfSets(final League league) {
        return league.getSeasons().stream()
                .mapToInt(season -> season.getRounds().stream()
                        .mapToInt(round -> round.getRoundGroups().stream()
                                .mapToInt(roundGroup -> roundGroup.getMatches().stream()
                                        .mapToInt(match -> match.getSetResults().size())
                                        .sum())
                                .sum())
                        .sum())
                .sum();
    }
}
