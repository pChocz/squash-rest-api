package com.pj.squashrestapp.model.entityhelper;

import com.pj.squashrestapp.model.Match;

/**
 *
 */
public class MatchValidator {

  final private SetStatus setStatus1;
  final private SetStatus setStatus2;
  final private SetStatus setStatus3;

  public MatchValidator(final Match match) {
    this.setStatus1 = getSetResult(match, 1).checkStatus();
    this.setStatus2 = getSetResult(match, 2).checkStatus();
    this.setStatus3 = getSetResult(match, 3).checkStatus();
  }

  private SetValidator getSetResult(final Match match, final int number) {
    return match
            .getSetResults()
            .stream()
            .filter(set -> set.getNumber() == number)
            .findFirst()
            .map(SetValidator::new)
            .orElse(null);
  }

  public MatchStatus checkStatus() {

    if (allSetsEmpty()) {
      return MatchStatus.EMPTY;

    } else if (isFinished()) {
      return MatchStatus.FINISHED;

    } else if (anySetError() || missingSet() || toManySets()) {
      return MatchStatus.ERROR;

    } else {
      return MatchStatus.IN_PROGRESS;

    }
  }

  private boolean toManySets() {
    if (setStatus1 == SetStatus.FIRST_PLAYER_WINS
        && setStatus2 == SetStatus.FIRST_PLAYER_WINS
        && setStatus3 != SetStatus.EMPTY) {

      return true;

    } else if (setStatus1 == SetStatus.SECOND_PLAYER_WINS
               && setStatus2 == SetStatus.SECOND_PLAYER_WINS
               && setStatus3 != SetStatus.EMPTY) {

      return true;

    } else {
      return false;

    }
  }

  private boolean missingSet() {
    //todo: implement
    return false;
  }

  private boolean allSetsEmpty() {
    return setStatus1 == SetStatus.EMPTY
           && setStatus2 == SetStatus.EMPTY
           && setStatus3 == SetStatus.EMPTY;
  }

  private boolean isFinished() {
    if (wonAfterTwoSets(SetStatus.FIRST_PLAYER_WINS)
        || wonAfterTwoSets(SetStatus.SECOND_PLAYER_WINS)) {

      return true;

    } else if (wonAfterTiebreak(SetStatus.FIRST_PLAYER_WINS, SetStatus.SECOND_PLAYER_WINS)
               || wonAfterTiebreak(SetStatus.SECOND_PLAYER_WINS, SetStatus.FIRST_PLAYER_WINS)) {

      return true;

    } else {
      return false;

    }
  }

  private boolean anySetError() {
    return setStatus1 == SetStatus.ERROR
           || setStatus2 == SetStatus.ERROR
           || setStatus3 == SetStatus.ERROR;
  }

  private boolean wonAfterTwoSets(final SetStatus setStatus) {
    return setStatus1 == setStatus
           && setStatus2 == setStatus
           && setStatus3 == SetStatus.EMPTY;
  }

  private boolean wonAfterTiebreak(final SetStatus first, final SetStatus second) {
    return setStatus1 == first
           && setStatus2 == second
           && (setStatus3 == first || setStatus3 == second);
  }

}
