package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.LostBallsAggregatedForSeason;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.SeasonDto;
import com.pj.squashrestapp.dto.XpPointsForTable;
import com.pj.squashrestapp.dto.scoreboard.RoundAndGroupPosition;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonStar;
import com.pj.squashrestapp.dto.scoreboard.Type;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.GeneralUtil;
import com.pj.squashrestapp.util.JacksonUtil;
import com.pj.squashrestapp.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeasonService {

    private final BonusPointService bonusPointService;
    private final LostBallService lostBallService;
    private final XpPointsService xpPointsService;

    private final DeepRemovalService deepRemovalService;
    private final SetResultRepository setResultRepository;
    private final SeasonRepository seasonRepository;
    private final LeagueRepository leagueRepository;

    public SeasonScoreboardDto getSeasonScoreboardDtoForLeagueStats(
            final Season season,
            final ArrayListMultimap<String, Integer> xpPointsPerSplit,
            final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason,
            final LostBallsAggregatedForSeason lostBallsAggregatedForSeason) {
        final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(season);
        return buildSeasonScoreboardDto(
                seasonScoreboardDto,
                season,
                xpPointsPerSplit,
                bonusPointsAggregatedForSeason,
                lostBallsAggregatedForSeason);
    }

    public UUID extractLeagueUuid(final UUID seasonUuid) {
        return seasonRepository.retrieveLeagueUuidOfSeason(seasonUuid);
    }

    public SeasonScoreboardDto buildSeasonScoreboardDto(
            final SeasonScoreboardDto seasonScoreboardDto,
            final Season season,
            final ArrayListMultimap<String, Integer> xpPointsPerSplit,
            final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason,
            final LostBallsAggregatedForSeason lostBallsAggregatedForSeason) {

        for (final Round round : season.getFinishedRoundsOrdered()) {
            // remove uber-star immediately (as it's valid for 1 round only)
            seasonScoreboardDto.getSeasonStars().values().removeIf(star -> star.getType() == Type.UBER);

            final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
            final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
            final String split = GeneralUtil.integerListToString(playersPerGroup);
            final List<Integer> xpPoints = xpPointsPerSplit.get(split + "|" + season.getXpPointsType());
            roundScoreboard.assignPointsAndPlaces(xpPoints);

            for (final RoundGroupScoreboard scoreboard : roundScoreboard.getRoundGroupScoreboards()) {
                for (final RoundGroupScoreboardRow scoreboardRow : scoreboard.getScoreboardRows()) {
                    final PlayerDto player = scoreboardRow.getPlayer();
                    final SeasonScoreboardRowDto seasonScoreboardRowDto =
                            seasonScoreboardDto.getSeasonScoreboardRows().stream()
                                    .filter(p -> p.getPlayer().equals(player))
                                    .findFirst()
                                    .orElse(new SeasonScoreboardRowDto(
                                            player, bonusPointsAggregatedForSeason, lostBallsAggregatedForSeason));

                    seasonScoreboardRowDto.addScoreboardRow(scoreboardRow);

                    // if it's not the first group, count pretenders points as well
                    if (scoreboardRow.getPlaceInGroup() != scoreboardRow.getPlaceInRound()) {
                        seasonScoreboardRowDto.addXpForRoundPretendents(round.getNumber(), scoreboardRow.getXpEarned());
                    }

                    // removing stars for each player that has played in that group
                    seasonScoreboardDto.getSeasonStars().remove(player.getUuid());

                    final int roundNumber = round.getNumber();
                    final int groupNumber = scoreboard.getRoundGroupNumber();
                    final int placeInGroup = scoreboardRow.getPlaceInGroup();
                    final int placeInRound = scoreboardRow.getPlaceInRound();
                    final boolean isFirstPlace = placeInGroup == 1;
                    final boolean isLastPlace = scoreboard.getScoreboardRows().indexOf(scoreboardRow)
                            == scoreboard.getScoreboardRows().size() - 1;

                    // add stars for first and last places
                    if (isFirstPlace && groupNumber == 1) {
                        final SeasonStar seasonStar =
                                new SeasonStar(roundNumber, String.valueOf((char) (groupNumber + 'A' - 1)), Type.UBER);
                        seasonScoreboardDto.getSeasonStars().put(player.getUuid(), seasonStar);
                    } else if (isFirstPlace) {
                        final SeasonStar seasonStar = new SeasonStar(
                                roundNumber, String.valueOf((char) (groupNumber + 'A' - 2)), Type.PROMOTION);
                        seasonScoreboardDto.getSeasonStars().put(player.getUuid(), seasonStar);
                    } else if (isLastPlace) {
                        final SeasonStar seasonStar = new SeasonStar(
                                roundNumber, String.valueOf((char) (groupNumber + 'A')), Type.RELEGATION);
                        seasonScoreboardDto.getSeasonStars().put(player.getUuid(), seasonStar);
                    }

                    seasonScoreboardRowDto.addXpForRound(
                            round.getNumber(),
                            new RoundAndGroupPosition(
                                    String.valueOf((char) (groupNumber + 'A' - 1)),
                                    placeInGroup,
                                    placeInRound,
                                    scoreboardRow.getXpEarned(),
                                    isLastPlace));
                    final boolean containsPlayer =
                            seasonScoreboardDto.getSeasonScoreboardRows().contains(seasonScoreboardRowDto);
                    if (!containsPlayer) {
                        seasonScoreboardDto.getSeasonScoreboardRows().add(seasonScoreboardRowDto);
                    }
                }
            }
            // remove all stars if it's the last round of the season
            final boolean isSeasonFinished = round.getNumber() == seasonScoreboardDto.getAllRounds();
            if (isSeasonFinished) {
                seasonScoreboardDto.getSeasonStars().clear();
            }
        }

        for (final SeasonScoreboardRowDto seasonScoreboardRowDto : seasonScoreboardDto.getSeasonScoreboardRows()) {
            seasonScoreboardRowDto.calculateFinishedRow(seasonScoreboardDto.getCountedRounds());
        }

        seasonScoreboardDto.sortByCountedPoints();
        return seasonScoreboardDto;
    }

    @Cacheable(value = RedisCacheConfig.SEASON_SCOREBOARD_CACHE, key = "#seasonUuid")
    public SeasonScoreboardDto overalScoreboard(final UUID seasonUuid) {
        final SeasonScoreboardDto seasonScoreboardDto = buildSeasonScoreboardDto(seasonUuid);
        return seasonScoreboardDto;
    }

    public SeasonScoreboardDto buildSeasonScoreboardDto(final UUID seasonUuid) {
        final List<SetResult> setResultListForSeason = setResultRepository.fetchBySeasonUuid(seasonUuid);
        final Long seasonId = seasonRepository.findIdByUuid(seasonUuid);

        Season season = EntityGraphBuildUtil.reconstructSeason(setResultListForSeason, seasonId);
        if (season == null) {
            season = seasonRepository
                    .findSeasonByUuid(seasonUuid)
                    .orElseThrow(() -> new NoSuchElementException(ErrorCode.SEASON_NOT_FOUND));
        }

        final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

        final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason =
                bonusPointService.extractBonusPointsAggregatedForSeason(seasonUuid);

        final LostBallsAggregatedForSeason lostBallsAggregatedForSeason =
                lostBallService.extractLostBallsAggregatedForSeason(seasonUuid);

        final SeasonScoreboardDto seasonScoreboardDto = getSeasonScoreboardDto(
                season, xpPointsPerSplit, bonusPointsAggregatedForSeason, lostBallsAggregatedForSeason);
        return seasonScoreboardDto;
    }

    public SeasonScoreboardDto getSeasonScoreboardDto(
            final Season season,
            final ArrayListMultimap<String, Integer> xpPointsPerSplit,
            final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason,
            final LostBallsAggregatedForSeason lostBallsAggregatedForSeason) {

        final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(season);

        return buildSeasonScoreboardDto(
                seasonScoreboardDto,
                season,
                xpPointsPerSplit,
                bonusPointsAggregatedForSeason,
                lostBallsAggregatedForSeason);
    }

    public UUID getCurrentSeasonUuidForLeague(final UUID leagueUuid) {
        final List<Season> currentSeasonAsList =
                seasonRepository.findCurrentSeasonForLeague(leagueUuid, PageRequest.of(0, 1));
        if (currentSeasonAsList.isEmpty()) {
            return null;
        } else {
            return currentSeasonAsList.get(0).getUuid();
        }
    }

    @Transactional(readOnly = true)
    public SeasonDto extractSeasonDtoByUuid(final UUID seasonUuid) {
        final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
        final SeasonDto seasonDto = new SeasonDto(season);
        return seasonDto;
    }

    public List<PlayerDto> extractSeasonPlayers(final UUID seasonUuid) {
        final Set<PlayerDto> playersFirst = seasonRepository.extractSeasonPlayersFirst(seasonUuid).stream()
                .map(PlayerDto::new)
                .collect(Collectors.toSet());

        final Set<PlayerDto> playersSecond = seasonRepository.extractSeasonPlayersSecond(seasonUuid).stream()
                .map(PlayerDto::new)
                .collect(Collectors.toSet());

        final List<PlayerDto> merged = Stream.concat(playersFirst.stream(), playersSecond.stream())
                .distinct()
                .sorted(Comparator.comparing(PlayerDto::getUsername))
                .collect(Collectors.toList());

        return merged;
    }

    @Transactional
    public Season createNewSeason(
            final int seasonNumber,
            final LocalDate startDate,
            final UUID leagueUuid,
            final String xpPointsType,
            final String description) {
        final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
        final Season season = new Season(seasonNumber, startDate, xpPointsType, league);
        if (description != null) {
            season.setDescription(description);
        }
        league.addSeason(season);

        try {
            season.createAudit();
            seasonRepository.save(season);
            LogUtil.logCreate(new SeasonDto(season));
            return season;

        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.SEASON_DUPLICATE_ERROR);
        }
    }

    public void updateSeason(
            final UUID seasonUuid, final Optional<String> description, final Optional<String> xpPointsType) {
        final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
        final Object seasonBefore = JacksonUtil.deepCopy(new SeasonDto(season));
        description.ifPresent(season::setDescription);
        if (xpPointsType.isPresent()) {
            List<String> seasonSplits = seasonRepository.extractRoundSplitsForSeason(seasonUuid).stream()
                    .distinct()
                    .toList();

            List<String> xpPointsSplits = xpPointsService.buildXpPointsForTableForType(xpPointsType.get()).stream()
                    .map(XpPointsForTable::getSplit)
                    .distinct()
                    .toList();

            if (xpPointsSplits.containsAll(seasonSplits)) {
                season.setXpPointsType(xpPointsType.get());
            }
        }
        season.updateAudit();
        seasonRepository.save(season);
        LogUtil.logModify(seasonBefore, new SeasonDto(season));
    }

    public void deleteSeason(final UUID seasonUuid) {
        deepRemovalService.deepRemoveSeason(seasonUuid);
    }

    public Pair<Optional<UUID>, Optional<UUID>> extractAdjacentSeasonsUuids(UUID seasonUuid) {
        final Season season = seasonRepository.findByUuidWithLeague(seasonUuid);
        if (season == null) {
            return Pair.of(Optional.empty(), Optional.empty());
        }

        final UUID previousSeasonUuid = seasonRepository
                .findByLeagueAndNumber(season.getLeague(), season.getNumber() - 1)
                .map(Season::getUuid)
                .orElse(null);

        final UUID nextSeasonUuid = seasonRepository
                .findByLeagueAndNumber(season.getLeague(), season.getNumber() + 1)
                .map(Season::getUuid)
                .orElse(null);

        return Pair.of(Optional.ofNullable(previousSeasonUuid), Optional.ofNullable(nextSeasonUuid));
    }

    public List<UUID> extractAllSeasonsUuidsForLeague(UUID leagueUuid) {
        return seasonRepository.retrieveSeasonsUuidsOfLeagueUuid(leagueUuid);
    }

    public List<String> extractSeasonSplits(UUID seasonUuid) {
        return seasonRepository.extractRoundSplitsForSeason(seasonUuid);
    }
}
