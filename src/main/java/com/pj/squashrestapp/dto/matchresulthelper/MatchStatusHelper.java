package com.pj.squashrestapp.dto.matchresulthelper;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.dto.match.SetDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/** */
@Slf4j
@UtilityClass
public class MatchStatusHelper {

    public MatchStatus checkStatus(final MatchDto match) {

        final int numberOfSets = match.getSets().size();
        final int maxNumberOfSets = match.getMatchFormatType().getMaxNumberOfSets();

        if (numberOfSets != maxNumberOfSets) {
            log.error("Some inconsistency in number of sets within a match! {}", match);
            return MatchStatus.ERROR;
        }

        final List<SetStatus> setStatuses = match.getSets().stream()
                .map(setDto -> {
                    if (setDto.getSetNumber() < numberOfSets) {
                        // regular set
                        return SetStatusHelper.checkStatus(
                                setDto, match.getRegularSetWinningPoints(), match.getRegularSetWinningType());
                    } else {
                        // tiebreak
                        return SetStatusHelper.checkStatus(
                                setDto, match.getTieBreakWinningPoints(), match.getTieBreakWinningType());
                    }
                })
                .toList();

        return verifySetStatuses(setStatuses);
  }

    private static MatchStatus verifySetStatuses(final List<SetStatus> setStatuses) {

        if (setStatuses.stream().allMatch(setStatus -> setStatus == SetStatus.EMPTY)) {
            return MatchStatus.EMPTY;
        }

        if (setStatuses.stream().anyMatch(setStatus -> setStatus == SetStatus.ERROR)) {
            return MatchStatus.ERROR;
        }

        return determineMatchStatus(setStatuses);
  }

    @SuppressWarnings({"MethodWithMultipleReturnPoints", "OverlyLongMethod", "OverlyComplexMethod"})
    private MatchStatus determineMatchStatus(final List<SetStatus> setStatuses) {
    final int numberOfSets = setStatuses.size();
    final int numberOfSetsToWinMatch = numberOfSets / 2 + 1;

    int firstPlayerWonSets = 0;
    int secondPlayerWonSets = 0;
    int numberOfEmptySetsInBetween = 0;

    for (int i=0; i<numberOfSets; i++) {

      boolean isCurrentSetInProgress = false;

      switch (setStatuses.get(i)) {
        case FIRST_PLAYER_WINS -> firstPlayerWonSets++;
        case SECOND_PLAYER_WINS -> secondPlayerWonSets++;
        case IN_PROGRESS -> isCurrentSetInProgress = true;
        case EMPTY -> numberOfEmptySetsInBetween++;
      }

      final boolean isEnoughSetsFinished = firstPlayerWonSets == numberOfSetsToWinMatch
          || secondPlayerWonSets == numberOfSetsToWinMatch;

      final boolean noEmptySetsInBetween = numberOfEmptySetsInBetween == 0;
      final boolean isFinalSetOrAreRemainingEmpty = isFinalOrAreRemainingSetsEmpty(i, setStatuses);

      if (isEnoughSetsFinished && isFinalSetOrAreRemainingEmpty && noEmptySetsInBetween) {
        return MatchStatus.FINISHED;
      } else if (isEnoughSetsFinished) {
        return MatchStatus.ERROR;
      }

      if (isCurrentSetInProgress && isFinalSetOrAreRemainingEmpty && noEmptySetsInBetween) {
        return MatchStatus.IN_PROGRESS;
      } else if (isCurrentSetInProgress) {
        return MatchStatus.ERROR;
      }

      if (isFinalSetOrAreRemainingEmpty) {
        return MatchStatus.IN_PROGRESS;
      }
    }

    return MatchStatus.ERROR;
  }

  private boolean isFinalOrAreRemainingSetsEmpty(final int i, final List<SetStatus> setStatuses) {
    return i == setStatuses.size() - 1
        || areRemainingSetsOfType(i, setStatuses, SetStatus.EMPTY);
  }

  private boolean areRemainingSetsOfType(final int i, final List<SetStatus> setStatuses, final SetStatus setStatus) {
    final List<SetStatus> remainingSets = setStatuses.subList(i + 1, setStatuses.size());
    return remainingSets.stream().allMatch(setStatusToCheck -> setStatusToCheck == setStatus);
  }

  public PlayerDto getWinner(final MatchDto match) {
    int firstPlayerWonSets = 0;
    int secondPlayerWonSets = 0;

    for (final SetDto set : match.getSets()) {
      if (set.getFirstPlayerScoreNullSafe() > set.getSecondPlayerScoreNullSafe()) {
        firstPlayerWonSets++;
      } else {
        secondPlayerWonSets++;
      }
    }

    if (firstPlayerWonSets > secondPlayerWonSets) {
      return match.getFirstPlayer();
    } else {
      return match.getSecondPlayer();
    }
  }

}
