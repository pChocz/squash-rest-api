package com.pj.squashrestapp.dto.matchresulthelper;

import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetWinningType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@UtilityClass
public class MatchStatusHelper {

  private final static List<SetStatus> FIRST_PLAYER_WINS_AFTER_2_SETS = List.of(
      SetStatus.FIRST_PLAYER_WINS,
      SetStatus.FIRST_PLAYER_WINS,
      SetStatus.EMPTY);

  private final static List<SetStatus> SECOND_PLAYER_WINS_AFTER_2_SETS = List.of(
      SetStatus.SECOND_PLAYER_WINS,
      SetStatus.SECOND_PLAYER_WINS,
      SetStatus.EMPTY);

  private final static List<SetStatus> FIRST_PLAYER_WINS_AFTER_3_SETS_TIEBREAK_A = List.of(
      SetStatus.FIRST_PLAYER_WINS,
      SetStatus.SECOND_PLAYER_WINS,
      SetStatus.FIRST_PLAYER_WINS);

  private final static List<SetStatus> FIRST_PLAYER_WINS_AFTER_3_SETS_TIEBREAK_B = List.of(
      SetStatus.SECOND_PLAYER_WINS,
      SetStatus.FIRST_PLAYER_WINS,
      SetStatus.FIRST_PLAYER_WINS);

  private final static List<SetStatus> SECOND_PLAYER_WINS_AFTER_3_SETS_TIEBREAK_A = List.of(
      SetStatus.FIRST_PLAYER_WINS,
      SetStatus.SECOND_PLAYER_WINS,
      SetStatus.SECOND_PLAYER_WINS);

  private final static List<SetStatus> SECOND_PLAYER_WINS_AFTER_3_SETS_TIEBREAK_B = List.of(
      SetStatus.SECOND_PLAYER_WINS,
      SetStatus.FIRST_PLAYER_WINS,
      SetStatus.SECOND_PLAYER_WINS);

  public static MatchStatus checkStatus(
      final MatchDto match,
      final MatchFormatType matchFormatType,
      final SetWinningType regularSetWinningType,
      final SetWinningType tiebreakWinningType,
      final int regularSetWinningPoints,
      final int tiebreakWinningPoints) {

    final int numberOfSets = match.getSets().size();
    final int maxNumberOfSets = matchFormatType.getMaxNumberOfSets();

    if (numberOfSets != maxNumberOfSets) {
      log.error("Some inconsistency in number of sets within a match! {}", match);
      return MatchStatus.ERROR;
    }

    final List<SetStatus> setStatuses =
        match.getSets().stream()
            .map(
                setDto -> {
                  if (setDto.getSetNumber() < numberOfSets) {
                    // regular set
                    return SetStatusHelper.checkStatus(
                        setDto, regularSetWinningPoints, regularSetWinningType);
                  } else {
                    // tiebreak
                    return SetStatusHelper.checkStatus(
                        setDto, tiebreakWinningPoints, tiebreakWinningType);
                  }
                })
            .collect(Collectors.toList());

    return verifySetStatuses(matchFormatType, setStatuses);
  }

  private static MatchStatus verifySetStatuses(
      final MatchFormatType matchFormatType, final List<SetStatus> setStatuses) {

    if (setStatuses.stream().allMatch(setStatus -> setStatus == SetStatus.EMPTY)) {
      return MatchStatus.EMPTY;
    }



    if (setStatuses.stream().anyMatch(setStatus -> setStatus == SetStatus.ERROR)) {
      return MatchStatus.ERROR;
    }

    return switch (matchFormatType) {
      case ONE_GAME -> checkStatusForOneGame(setStatuses);
      case BEST_OF_3 -> checkStatusForBestOf3(setStatuses);
      case BEST_OF_5 -> checkStatusForBestOf5(setStatuses);
    };

  }

  private static MatchStatus checkStatusForOneGame(final List<SetStatus> setStatuses) {
    final SetStatus theOnlySetStatus = setStatuses.get(0);
    if (theOnlySetStatus == SetStatus.FIRST_PLAYER_WINS || theOnlySetStatus == SetStatus.SECOND_PLAYER_WINS) {
      return MatchStatus.FINISHED;
    } else {
      return MatchStatus.ERROR;
    }
  }

  private static MatchStatus checkStatusForBestOf3(final List<SetStatus> setStatuses) {
    final SetStatus firstSetStatus = setStatuses.get(0);
    final SetStatus secondSetStatus = setStatuses.get(1);
    final SetStatus thirdSetStatus = setStatuses.get(2);

    final int numberOfSetsToWinMatch = 2;

    int firstPlayerWonSets = 0;
    int secondPlayerWonSets = 0;

    for (int i=0; i<setStatuses.size(); i++) {
      final SetStatus setStatus = setStatuses.get(i);

      if (setStatus == SetStatus.FIRST_PLAYER_WINS) {
        firstPlayerWonSets++;
      } else if (setStatus == SetStatus.SECOND_PLAYER_WINS) {
        secondPlayerWonSets++;
      }

      if (firstPlayerWonSets == numberOfSetsToWinMatch || secondPlayerWonSets == numberOfSetsToWinMatch) {
        final boolean areRemainingSetsEmpty = checkRemainingSetsEmpty(i, setStatuses);
        if (areRemainingSetsEmpty) {
          return MatchStatus.FINISHED;
        } else {
          return MatchStatus.ERROR;
        }
      }



    }

    if (firstSetStatus == SetStatus.FIRST_PLAYER_WINS
        && secondSetStatus == SetStatus.FIRST_PLAYER_WINS
        && thirdSetStatus == SetStatus.EMPTY) {

      // first player wins without tiebreak
      return MatchStatus.FINISHED;

    } else if (firstSetStatus == SetStatus.SECOND_PLAYER_WINS
        && secondSetStatus == SetStatus.SECOND_PLAYER_WINS
        && thirdSetStatus == SetStatus.EMPTY) {

      // second player wins without tiebreak
      return MatchStatus.FINISHED;

    } else if (firstSetStatus == SetStatus.FIRST_PLAYER_WINS
        && secondSetStatus == SetStatus.SECOND_PLAYER_WINS
        && thirdSetStatus == SetStatus.FIRST_PLAYER_WINS) {

      // first player wins after the tiebreak
      return MatchStatus.FINISHED;

    } else if (firstSetStatus == SetStatus.FIRST_PLAYER_WINS
        && secondSetStatus == SetStatus.SECOND_PLAYER_WINS
        && thirdSetStatus == SetStatus.SECOND_PLAYER_WINS) {

      // second player wins after the tiebreak
      return MatchStatus.FINISHED;

    }

    // match is not finished for sure. It's also not EMPTY nor ERROR as it was check earlier
    // Is the only case possible that it's IN PROGRESS? TODO: verify that!

    return MatchStatus.IN_PROGRESS;
  }

  private static boolean checkRemainingSetsEmpty(final int i, final List<SetStatus> setStatuses) {
    final List<SetStatus> remainingSets = setStatuses.subList(i + 1, setStatuses.size());
    return remainingSets.stream().allMatch(setStatus -> setStatus == SetStatus.EMPTY);
  }


  private static MatchStatus checkStatusForBestOf5(final List<SetStatus> setStatuses) {
    final SetStatus firstSetStatus = setStatuses.get(0);
    final SetStatus secondSetStatus = setStatuses.get(1);
    final SetStatus thirdSetStatus = setStatuses.get(2);
    final SetStatus fourthSetStatus = setStatuses.get(3);
    final SetStatus fifthSetStatus = setStatuses.get(4);

    return null;
  }

}
