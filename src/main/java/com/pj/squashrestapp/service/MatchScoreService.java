package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatusHelper;
import com.pj.squashrestapp.dto.matchresulthelper.SetStatus;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.MatchScore;
import com.pj.squashrestapp.model.ScoreEventType;
import com.pj.squashrestapp.model.ServePlayer;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchScoreService {

    private final MatchRepository matchRepository;
    private final RedisCacheService redisCacheService;


    @Transactional
    public MatchDetailedDto appendNewScore(final UUID matchUuid, final MatchScore matchScore) {
        validateCompleteness(matchScore);
        matchScore.setZonedDateTime(ZonedDateTime.now(GeneralUtil.UTC_ZONE_ID));

        final Match match = matchRepository
                .findMatchByUuidWithScoreSheet(matchUuid)
                .orElseThrow(() -> new GeneralBadRequestException(ErrorCode.MATCH_NOT_FOUND));

        final MatchSimpleDto matchSimpleDto = new MatchSimpleDto(match);

        final List<SetStatus> gameStatuses = MatchStatusHelper.getSetStatuses(matchSimpleDto);
        final int firstPlayerWonGames = (int) gameStatuses.stream().filter(s -> s == SetStatus.FIRST_PLAYER_WINS).count();
        final int secondPlayerWonGames = (int) gameStatuses.stream().filter(s -> s == SetStatus.SECOND_PLAYER_WINS).count();

        final ScoreEventType scoreEventType = matchScore.getScoreEventType();

        final Optional<MatchScore> lastScore = match.getLastScore();

        matchScore.setFirstPlayerGamesWon(firstPlayerWonGames);
        matchScore.setSecondPlayerGamesWon(secondPlayerWonGames);
        matchScore.setMatchFinished(matchSimpleDto.checkFinished());

        if (scoreEventType == ScoreEventType.MATCH_BEGINS) {
            if (match.getScores().isEmpty() && matchSimpleDto.getStatus() == MatchStatus.EMPTY) {
                matchScore.setCanScore(false);
                matchScore.setCanStartGame(true);
                matchScore.setCanEndGame(false);
                matchScore.setCanEndMatch(false);
                return applyMatchScore(matchScore, match, gameStatuses);
            } else {
                throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
            }

        } else if (scoreEventType == ScoreEventType.GAME_BEGINS) {
            if (lastScore.isPresent() && lastScore.get().isCanStartGame()) {
                final Optional<MatchScore> lastRallyMatchScore = match
                        .getScores()
                        .stream()
                        .filter(MatchScore::isRally)
                        .reduce((first, second) -> second);
                if (lastRallyMatchScore.isPresent()) {
                    if (lastRallyMatchScore.get().isFirstPlayerScored()) {
                        matchScore.setNextSuggestedServePlayer(ServePlayer.FIRST_PLAYER);
                    } else if (lastRallyMatchScore.get().isSecondPlayerScored()) {
                        matchScore.setNextSuggestedServePlayer(ServePlayer.SECOND_PLAYER);
                    }
                }
                matchScore.setCanScore(true);
                matchScore.setCanStartGame(false);
                matchScore.setCanEndGame(false);
                matchScore.setCanEndMatch(false);
                return applyMatchScore(matchScore, match, gameStatuses);
            } else {
                throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
            }

        } else if (scoreEventType.isRally()) {
            if (lastScore.isPresent() && lastScore.get().isCanScore()) {
                matchScore.setCanStartGame(false);
                matchScore.setCanEndMatch(false);
                return applyMatchScore(matchScore, match, gameStatuses);
            } else {
                throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
            }

        } else if (scoreEventType == ScoreEventType.GAME_ENDS) {
            if (lastScore.isPresent() && lastScore.get().isCanEndGame()) {
                matchScore.setCanScore(false);
                matchScore.setCanStartGame(!matchSimpleDto.checkFinished());
                matchScore.setCanEndGame(false);
                matchScore.setCanEndMatch(matchSimpleDto.checkFinished());
                return applyMatchScore(matchScore, match, gameStatuses);
            } else {
                throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
            }

        } else if (scoreEventType == ScoreEventType.MATCH_ENDS) {
            if (lastScore.isPresent() && lastScore.get().isCanEndMatch()) {
                matchScore.setCanScore(false);
                matchScore.setCanStartGame(false);
                matchScore.setCanEndGame(false);
                matchScore.setCanEndMatch(false);
                return applyMatchScore(matchScore, match, gameStatuses);
            } else {
                throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
            }
        }
        throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
    }

    private void validateCompleteness(final MatchScore matchScore) {
        final ScoreEventType scoreEventType = matchScore.getScoreEventType();

        if (scoreEventType == null) {
            // scoreEventType must be provided
            throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
        }

        if (!matchScore.isRally()
                && matchScore.getAppealDecision() != null
                && matchScore.getServeSide() != null
                && matchScore.getServePlayer() != null) {
            // non-rally score type must not have additional parameters provided
            throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
        }

        if (scoreEventType == ScoreEventType.FIRST_PLAYER_CALLS_LET
                || scoreEventType == ScoreEventType.SECOND_PLAYER_CALLS_LET) {
            // let-request must have appealDecision
            if (matchScore.getAppealDecision() == null) {
                throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
            }

        } else if (scoreEventType == ScoreEventType.FIRST_PLAYER_SCORES
                || scoreEventType == ScoreEventType.SECOND_PLAYER_SCORES) {
            // regular scores must not have appealDecision
            if (matchScore.getAppealDecision() != null) {
                throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
            }
        }

        // rally type must have serve information
        if (scoreEventType.isRally() &&
                (matchScore.getServeSide() == null || matchScore.getServePlayer() == null)) {
            throw new GeneralBadRequestException(ErrorCode.INVALID_MATCH_SCORE);
        }
    }

    private MatchDetailedDto applyMatchScore(final MatchScore matchScore,
                                             final Match match,
                                             final List<SetStatus> fdsfsfdsfs) {
        log.info("BEFORE: " + match);

        final SetResult setResult;
        if (matchScore.getScoreEventType() == ScoreEventType.MATCH_BEGINS
                || matchScore.getScoreEventType() == ScoreEventType.MATCH_ENDS) {

            setResult = null;

        } else {
            final Optional<MatchScore> lastScore = match.getLastScore();
            if (lastScore.isPresent() && lastScore.get().getGameNumber() != null) {
                // we are in the middle of the game
                setResult = match.getSetResultsOrdered().get(
                        lastScore.get().getScoreEventType() == ScoreEventType.GAME_ENDS
                                // if last score is GAME_ENDS we take the next one
                                ? lastScore.get().getGameNumber()
                                // otherwise we take current one
                                : lastScore.get().getGameNumber() - 1);
                if (setResult.getFirstPlayerScore() == null && setResult.getSecondPlayerScore() == null) {
                    setResult.setFirstPlayerScore(0);
                    setResult.setSecondPlayerScore(0);
                }
            } else {
                // we are at the very beginning of the match
                setResult = new SetResult(1, 0, 0);
            }
        }

        if (matchScore.getScoreEventType() == ScoreEventType.GAME_BEGINS
                || matchScore.getScoreEventType() == ScoreEventType.GAME_ENDS) {

            matchScore.setFirstPlayerScore(setResult.getFirstPlayerScore());
            matchScore.setSecondPlayerScore(setResult.getSecondPlayerScore());
            matchScore.setGameNumber(setResult.getNumber());

        } else if (matchScore.getScoreEventType().isRally()) {

            matchScore.setGameNumber(setResult.getNumber());

            // need to update score
            if (matchScore.isFirstPlayerScored()) {
                setResult.setFirstPlayerScore(setResult.getFirstPlayerScore() + 1);
                matchScore.setNextSuggestedServePlayer(ServePlayer.FIRST_PLAYER);

            } else if (matchScore.isSecondPlayerScored()) {
                setResult.setSecondPlayerScore(setResult.getSecondPlayerScore() + 1);
                matchScore.setNextSuggestedServePlayer(ServePlayer.SECOND_PLAYER);

            } else {
                // must be YES_LET - player repeats serve
                matchScore.setNextSuggestedServePlayer(matchScore.getServePlayer());
            }

            final List<SetStatus> gameStatuses = MatchStatusHelper.getSetStatuses(new MatchSimpleDto(match));
            final SetStatus currentGameStatus = gameStatuses.get(setResult.getNumber() - 1);

            matchScore.setCanScore(currentGameStatus == SetStatus.IN_PROGRESS || currentGameStatus == SetStatus.EMPTY);
            matchScore.setCanEndGame(currentGameStatus == SetStatus.FIRST_PLAYER_WINS || currentGameStatus == SetStatus.SECOND_PLAYER_WINS);

            matchScore.setFirstPlayerScore(setResult.getFirstPlayerScore());
            matchScore.setSecondPlayerScore(setResult.getSecondPlayerScore());
        }

        match.addScore(matchScore);

        redisCacheService.evictCacheForRoundMatch(match);

        return persistMatch(match);
    }

    private MatchDetailedDto persistMatch(final Match match) {
        final Match savedMatch = matchRepository.save(match);
        log.info("AFTER:  " + savedMatch);
        return new MatchDetailedDto(savedMatch);
    }

    private void modifyMatchResultIfNeeded(final MatchScore matchScore, final Match match, final MatchScore scoreToRemove) {

        final MatchScore matchScoreToProcess = scoreToRemove.getScoreEventType() == ScoreEventType.GAME_BEGINS
                ? scoreToRemove
                : matchScore;

        final Optional<SetResult> setToModify = match.getSetResults().stream()
                .filter(set -> matchScoreToProcess.getGameNumber() != null
                        && matchScoreToProcess.getGameNumber().equals(set.getNumber()))
                .findFirst();

        if (setToModify.isPresent()) {
            if (scoreToRemove.getScoreEventType() == ScoreEventType.GAME_BEGINS) {
                setToModify.get().setFirstPlayerScore(null);
                setToModify.get().setSecondPlayerScore(null);
            } else {
                setToModify.get().setFirstPlayerScore(matchScoreToProcess.getFirstPlayerScore());
                setToModify.get().setSecondPlayerScore(matchScoreToProcess.getSecondPlayerScore());
            }
        }
    }

    @Transactional
    public MatchDetailedDto revertLastScore(final UUID matchUuid) {
        final Match match = matchRepository
                .findMatchByUuidWithScoreSheet(matchUuid)
                .orElseThrow(() -> new GeneralBadRequestException(ErrorCode.MATCH_NOT_FOUND));

        final Optional<MatchScore> scoreToRemove = match.getLastScore();

        if (scoreToRemove.isEmpty()) {
            throw new GeneralBadRequestException(ErrorCode.NO_EXISTING_MATCH_SCORE);
        }

        log.info("BEFORE: " + match);

        match.getScores().remove(scoreToRemove.get());
        match.getLastScore().ifPresent(matchScore -> modifyMatchResultIfNeeded(matchScore, match, scoreToRemove.get()));

        redisCacheService.evictCacheForRoundMatch(match);

        return persistMatch(match);
    }

    @Transactional
    public MatchDetailedDto clearAll(final UUID matchUuid) {
        final Match match = matchRepository
                .findMatchByUuidWithScoreSheet(matchUuid)
                .orElseThrow(() -> new GeneralBadRequestException(ErrorCode.MATCH_NOT_FOUND));

        if (match.getScores().isEmpty()) {
            throw new GeneralBadRequestException(ErrorCode.NO_EXISTING_MATCH_SCORE);
        }

        log.info("BEFORE: " + match);

        Iterator<MatchScore> matchScoreIterator = match.getScores().iterator();
        while (matchScoreIterator.hasNext()) {
            matchScoreIterator.next();
            matchScoreIterator.remove();
        }

        for (final SetResult setResult : match.getSetResults()) {
            setResult.setFirstPlayerScore(null);
            setResult.setSecondPlayerScore(null);
        }

        redisCacheService.evictCacheForRoundMatch(match);

        return persistMatch(match);
    }

    public MatchDetailedDto getMatchWithScores(final UUID matchUuid) {
        final Match match = matchRepository
                .findMatchByUuidWithScoreSheet(matchUuid)
                .orElseThrow(() -> new GeneralBadRequestException(ErrorCode.MATCH_NOT_FOUND));

        return new MatchDetailedDto(match);
    }

}
