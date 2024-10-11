package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForLeague;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.dto.LostBallsAggregatedForLeague;
import com.pj.squashrestapp.dto.LostBallsAggregatedForSeason;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreboardService {

    private final RoundRepository roundRepository;
    private final SetResultRepository setResultRepository;
    private final XpPointsRepository xpPointsRepository;
    private final XpPointsService xpPointsService;
    private final SeasonService seasonService;
    private final BonusPointService bonusPointService;
    private final LostBallService lostBallService;
    private final LeagueRepository leagueRepository;

    @Cacheable(value = RedisCacheConfig.LEAGUE_ALL_SEASONS_SCOREBOARDS, key = "#leagueUuid")
    public List<SeasonScoreboardDto> allSeasonsScoreboards(final UUID leagueUuid) {

        final League leagueRaw = leagueRepository.findByUuid(leagueUuid).orElseThrow();

        final BonusPointsAggregatedForLeague bonusPointsAggregatedForLeague =
                bonusPointService.extractBonusPointsAggregatedForLeague(leagueUuid);

        final LostBallsAggregatedForLeague lostBallsAggregatedForLeague =
                lostBallService.extractLostBallsAggregatedForLeague(leagueUuid);

        final List<SetResult> setResultListForLeague = setResultRepository.fetchByLeagueUuid(leagueUuid);
        final League leagueReconstructed =
                EntityGraphBuildUtil.reconstructLeague(setResultListForLeague, leagueRaw.getId());

        if (leagueReconstructed == null) {
            return new ArrayList<>();
        }

        final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

        final List<SeasonScoreboardDto> seasonScoreboardDtoList = new ArrayList<>();
        for (final Season season : leagueReconstructed.getSeasons()) {

            final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason =
                    bonusPointsAggregatedForLeague.forSeason(season.getUuid());

            final LostBallsAggregatedForSeason lostBallsAggregatedForSeason =
                    lostBallsAggregatedForLeague.forSeason(season.getUuid());

            final SeasonScoreboardDto scoreboardDto = new SeasonScoreboardDto(season);
            SeasonScoreboardDto seasonScoreboardDto = seasonService.buildSeasonScoreboardDto(
                    scoreboardDto,
                    season,
                    xpPointsPerSplit,
                    bonusPointsAggregatedForSeason,
                    lostBallsAggregatedForSeason);
            seasonScoreboardDtoList.add(seasonScoreboardDto);
        }

        return seasonScoreboardDtoList;
    }

    @Cacheable(value = RedisCacheConfig.LEAGUE_ALL_ROUNDS_SCOREBOARDS, key = "#leagueUuid")
    public List<RoundScoreboard> allRoundsScoreboards(final UUID leagueUuid) {
        final League leagueRaw = leagueRepository.findByUuid(leagueUuid).orElseThrow();
        List<SetResult> setResultListForLeague = setResultRepository.fetchByLeagueUuid(leagueUuid);
        League leagueReconstructed = EntityGraphBuildUtil.reconstructLeague(setResultListForLeague, leagueRaw.getId());

        if (leagueReconstructed == null) {
            return new ArrayList<>();
        }

        final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

        final List<RoundScoreboard> roundScoreboards = new ArrayList<>();
        for (final Season season : leagueReconstructed.getSeasons()) {
            for (final Round round : season.getRounds()) {
                if (round.isFinished()) {
                    RoundScoreboard roundScoreboard = new RoundScoreboard(round);
                    final String split = round.getSplit();
                    final String type = season.getXpPointsType();
                    final List<Integer> xpPoints = xpPointsPerSplit.get(split + '|' + type);
                    roundScoreboard.assignPointsAndPlaces(xpPoints);
                    roundScoreboards.add(roundScoreboard);
                }
            }
        }
        setResultListForLeague = null;
        leagueReconstructed = null;

        return roundScoreboards;
    }

    @Cacheable(value = RedisCacheConfig.ROUND_SCOREBOARD_CACHE, key = "#roundUuid")
    public RoundScoreboard buildScoreboardForRound(final UUID roundUuid) {
        final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(roundUuid);
        final Long roundId = roundRepository.findIdByUuid(roundUuid);

        Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
        if (round == null) {
            round = roundRepository
                    .findByUuid(roundUuid)
                    .orElseThrow(() -> new NoSuchElementException(ErrorCode.ROUND_NOT_FOUND));
        }

        final Season season = round.getSeason();

        final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
        final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
        final String split = GeneralUtil.integerListToString(playersPerGroup);
        final String type = season.getXpPointsType();
        final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplitAndType(split, type);

        roundScoreboard.assignPointsAndPlaces(xpPoints);
        return roundScoreboard;
    }

    public UUID getCurrentSeasonUuidForPlayer(final UUID playerUuid) {
        final List<Round> mostRecentRoundAsList =
                roundRepository.findMostRecentRoundOfPlayer(playerUuid, PageRequest.of(0, 1));
        if (mostRecentRoundAsList.isEmpty()) {
            return null;
        } else {
            return mostRecentRoundAsList.get(0).getSeason().getUuid();
        }
    }

    public UUID getMostRecentRoundUuidForPlayer(final UUID playerUuid) {
        final List<Round> mostRecentRoundAsList =
                roundRepository.findMostRecentRoundOfPlayer(playerUuid, PageRequest.of(0, 1));
        if (mostRecentRoundAsList.isEmpty()) {
            return null;
        } else {
            return mostRecentRoundAsList.get(0).getUuid();
        }
    }

    public UUID getMostRecentRoundUuidForLeague(final UUID leagueUuid) {
        final List<Round> mostRecentRoundAsList =
                roundRepository.findMostRecentRoundOfLeague(leagueUuid, PageRequest.of(0, 1));
        if (mostRecentRoundAsList.isEmpty()) {
            return null;
        } else {
            return mostRecentRoundAsList.get(0).getUuid();
        }
    }

    private RoundScoreboard buildRoundScoreboard(final List<Round> mostRecentRoundAsList) {
        if (mostRecentRoundAsList.isEmpty()) {
            return null;
        }

        final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(
                mostRecentRoundAsList.get(0).getUuid());
        final Long roundId =
                roundRepository.findIdByUuid(mostRecentRoundAsList.get(0).getUuid());

        Round mostRecentRound = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
        if (mostRecentRound == null) {
            mostRecentRound = roundRepository
                    .findByUuid(mostRecentRound.getUuid())
                    .orElseThrow(() -> new NoSuchElementException(ErrorCode.ROUND_NOT_FOUND));
        }

        final RoundScoreboard roundScoreboard = new RoundScoreboard(mostRecentRound);
        final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
        final String split = GeneralUtil.integerListToString(playersPerGroup);
        final String type = mostRecentRound.getSeason().getXpPointsType();
        final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplitAndType(split, type);

        roundScoreboard.assignPointsAndPlaces(xpPoints);
        return roundScoreboard;
    }

    public RoundScoreboard buildMostRecentRoundOfLeague(final UUID leagueUuid) {
        final List<Round> mostRecentRoundAsList =
                roundRepository.findMostRecentRoundOfLeague(leagueUuid, PageRequest.of(0, 1));
        return buildRoundScoreboard(mostRecentRoundAsList);
    }
}
