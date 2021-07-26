package com.pj.squashrestapp.dto.matchresulthelper;

import com.pj.squashrestapp.model.SetWinningType;
import lombok.experimental.UtilityClass;

/** */
@UtilityClass
public class SetScoreHelper {

  public Integer computeWinnerScore(final Integer looserScore, final int setWinningPoints, final SetWinningType setWinningType) throws WrongResultException {
    return switch (setWinningType) {
      case WINNING_POINTS_ABSOLUTE -> getScoreForWinningPointsAbsolute(looserScore, setWinningPoints);
      case ADV_OF_2_ABSOLUTE -> getScoreForAdvantageOf2Absolute(looserScore, setWinningPoints);
      case ADV_OF_2_OR_1_AT_THE_END -> getScoreForAdvantageOf2Or1AtTheEnd(looserScore, setWinningPoints);
    };
  }

  private Integer getScoreForWinningPointsAbsolute(final Integer looserScore, final int setWinningPoints) {
    if (looserScore < setWinningPoints) {
      return setWinningPoints;
    } else {
      throw new WrongResultException("Wrong looser points!");
    }
  }

  private Integer getScoreForAdvantageOf2Absolute(final Integer looserScore, final int setWinningPoints) {
    if (looserScore < setWinningPoints - 1) {
      return setWinningPoints;
    } else {
      return looserScore + 2;
    }
  }

  private Integer getScoreForAdvantageOf2Or1AtTheEnd(final Integer looserScore, final int setWinningPoints) {
    if (looserScore > setWinningPoints) {
      throw new WrongResultException("Wrong looser points!");
    } else if (setWinningPoints - looserScore > 1) {
      return setWinningPoints;
    } else {
      return setWinningPoints + 1;
    }
  }

}
