package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;
import com.pj.squashrestapp.dto.RoundDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatusHelper;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundGroupRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.GeneralUtil;
import com.pj.squashrestapp.util.JacksonUtil;
import com.pj.squashrestapp.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoundService {

    private final SeasonRepository seasonRepository;
    private final PlayerRepository playerRepository;
    private final RoundRepository roundRepository;
    private final RoundGroupRepository roundGroupRepository;

    public void deleteRound(final UUID roundUuid) {
        final Round roundToDelete = roundRepository.findByUuidWithSeasonLeague(roundUuid);
        roundRepository.delete(roundToDelete);
        LogUtil.logDelete(new RoundScoreboard(roundToDelete));
    }

    @Transactional
    public Round createRound(
            final int roundNumber, final LocalDate roundDate, final UUID seasonUuid, final List<UUID[]> playersUuids) {

        final List<List<Player>> playersPerGroup = getPlayersPerGroups(playersUuids);

        final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
        final League league = season.getLeague();
        final int setsPerMatch = league.getMatchFormatType().getMaxNumberOfSets();

        final Round round =
                createRoundForSeasonWithGivenPlayers(season, roundNumber, roundDate, playersPerGroup, setsPerMatch);
        season.addRound(round);

        try {
            round.createAudit();
            roundRepository.save(round);
            LogUtil.logCreate(new RoundScoreboard(round));
            return round;

        } catch (final DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.ROUND_DUPLICATE_ERROR);
        }
    }

    private List<List<Player>> getPlayersPerGroups(final List<UUID[]> playersUuids) {
        final UUID[] allPlayersUuids =
                playersUuids.stream().flatMap(Arrays::stream).toArray(UUID[]::new);

        final List<Player> allPlayers = playerRepository.findByUuids(allPlayersUuids);

        final List<Player> allPlayersOrdered = Arrays.stream(allPlayersUuids)
                .map(uuid -> allPlayers.stream()
                        .filter(p -> p.getUuid().equals(uuid))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toList());

        final List<List<Player>> playersPerGroup = playersUuids.stream()
                .filter(uuids -> uuids.length > 0)
                .map(uuid -> Arrays.stream(uuid).collect(Collectors.toList()))
                .map(uuidsForCurrentGroup -> allPlayersOrdered.stream()
                        .filter(player -> uuidsForCurrentGroup.contains(player.getUuid()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        return playersPerGroup;
    }

    private Round createRoundForSeasonWithGivenPlayers(
            final Season season,
            final int roundNumber,
            final LocalDate roundDate,
            final List<List<Player>> playersPerGroup,
            final int setsPerMatch) {

        final Round round = new Round();
        round.setNumber(roundNumber);
        round.setDate(roundDate);

        final List<Integer> countPerRound =
                playersPerGroup.stream().map(List::size).collect(Collectors.toList());

        round.setSplit(GeneralUtil.integerListToString(countPerRound));

        for (int i = 1; i <= playersPerGroup.size(); i++) {
            final RoundGroup roundGroup = createRoundGroup(season, playersPerGroup, i, setsPerMatch);
            round.addRoundGroup(roundGroup);
        }

        return round;
    }

    private RoundGroup createRoundGroup(
            final Season season,
            final List<List<Player>> playersPerGroup,
            final int groupNumber,
            final int setsPerMatch) {

        final RoundGroup roundGroup = new RoundGroup();
        roundGroup.setNumber(groupNumber);

        int matchNumber = 1;
        final List<Player> groupPlayers = playersPerGroup.get(groupNumber - 1);
        for (int j = 0; j < groupPlayers.size(); j++) {
            for (int k = j + 1; k < groupPlayers.size(); k++) {
                final Player firstPlayer = groupPlayers.get(j);
                final Player secondPlayer = groupPlayers.get(k);
                final Match match = createMatch(matchNumber++, firstPlayer, secondPlayer, setsPerMatch, season);
                roundGroup.addMatch(match);
            }
        }
        return roundGroup;
    }

    private Match createMatch(
            final int number,
            final Player firstPlayer,
            final Player secondPlayer,
            final int setsPerMatch,
            final Season season) {

        final Match match = new Match(firstPlayer, secondPlayer, season);
        match.setNumber(number);
        match.createAudit();

        for (int i = 1; i <= setsPerMatch; i++) {
            final SetResult setResult = new SetResult();
            setResult.setNumber(i);
            setResult.setFirstPlayerScore(null);
            setResult.setSecondPlayerScore(null);

            match.addSetResult(setResult);
        }
        return match;
    }

    public Round updateRoundFinishedState(final UUID roundUuid, final boolean finishedState) {
        final Round round = roundRepository.findByUuidWithSeasonLeague(roundUuid);
        final Object roundBefore = JacksonUtil.deepCopy(new RoundDto(round));
        if (finishedState && !allRoundMatchesFinished(round)) {
            throw new GeneralBadRequestException(ErrorCode.ROUND_MATCHES_NOT_FINISHED);
        }
        round.setFinished(finishedState);
        round.updateAudit();
        roundRepository.save(round);
        LogUtil.logModify(roundBefore, new RoundDto(round));
        return round;
    }

    private boolean allRoundMatchesFinished(final Round round) {
        for (final RoundGroup roundGroup : round.getRoundGroups()) {
            for (final Match match : roundGroup.getMatches()) {
                final MatchStatus matchStatus = MatchStatusHelper.checkStatus(new MatchSimpleDto(match));
                if (matchStatus != MatchStatus.FINISHED) {
                    return false;
                }
            }
        }
        return true;
    }

    public UUID extractLeagueUuid(final UUID roundUuid) {
        return roundRepository.retrieveLeagueUuidOfRound(roundUuid);
    }

    @Transactional
    public void recreateRound(final UUID roundUuid, final List<UUID[]> playersUuids) {
        final Round round = roundRepository.findByUuidWithSeasonLeague(roundUuid);
        final Object roundScoreboardBefore = JacksonUtil.deepCopy(new RoundScoreboard(round));
        final Season season = round.getSeason();
        final int setsPerMatch = season.getMatchFormatType().getMaxNumberOfSets();

        final List<List<Player>> playersPerGroup = getPlayersPerGroups(playersUuids);

        final List<Integer> countPerRound =
                playersPerGroup.stream().map(List::size).collect(Collectors.toList());

        round.setSplit(GeneralUtil.integerListToString(countPerRound));
        round.setFinished(false);
        round.updateAudit();

        // deleting old round groups
        final Iterator<RoundGroup> iterator = round.getRoundGroups().iterator();
        while (iterator.hasNext()) {
            final RoundGroup roundGroup = iterator.next();
            iterator.remove();
            roundGroupRepository.delete(roundGroup);
        }

        roundGroupRepository.flush();

        // creating new round groups
        for (int i = 1; i <= playersPerGroup.size(); i++) {
            final RoundGroup roundGroup = createRoundGroup(season, playersPerGroup, i, setsPerMatch);
            round.addRoundGroup(roundGroup);
            roundGroupRepository.save(roundGroup);
        }
        LogUtil.logModify(roundScoreboardBefore, new RoundScoreboard(round));
    }

    public Pair<Optional<UUID>, Optional<UUID>> extractAdjacentRoundsUuids(UUID roundUuid) {
        final Round round = roundRepository.findByUuidWithSeasonAndLeague(roundUuid);
        if (round == null) {
            return Pair.of(Optional.empty(), Optional.empty());
        }

        final int roundNumber = round.getNumber();
        final Season season = round.getSeason();
        final UUID leagueUuid = season.getLeague().getUuid();
        final int seasonNumber = season.getNumber();
        final int lastRoundNumber = season.getNumberOfRounds();
        final boolean isFirstRound = (roundNumber == 1);
        final boolean isLastRound = (roundNumber == lastRoundNumber);

        final UUID previousRoundUuid = isFirstRound
                ? getRoundUuidOrNull(leagueUuid, seasonNumber - 1, lastRoundNumber)
                : getRoundUuidOrNull(leagueUuid, seasonNumber, roundNumber - 1);

        final UUID nextRoundUuid = isLastRound
                ? getRoundUuidOrNull(leagueUuid, seasonNumber + 1, 1)
                : getRoundUuidOrNull(leagueUuid, seasonNumber, roundNumber + 1);

        return Pair.of(Optional.ofNullable(previousRoundUuid), Optional.ofNullable(nextRoundUuid));
    }

    private UUID getRoundUuidOrNull(UUID leagueUuid, int seasonNumber, int roundNumber) {
        return roundRepository
                .findBySeasonLeagueUuidAndSeasonNumberAndNumber(leagueUuid, seasonNumber, roundNumber)
                .map(Round::getUuid)
                .orElse(null);
    }

    public List<UUID> extractAllRoundsUuidsForLeague(UUID leagueUuid) {
        return roundRepository.retrieveRoundsUuidsOfLeagueUuid(leagueUuid);
    }
}
