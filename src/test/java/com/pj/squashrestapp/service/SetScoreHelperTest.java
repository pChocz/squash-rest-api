package com.pj.squashrestapp.service;

import static com.pj.squashrestapp.model.SetWinningType.ADV_OF_2_ABSOLUTE;
import static com.pj.squashrestapp.model.SetWinningType.ADV_OF_2_OR_1_AT_THE_END;
import static com.pj.squashrestapp.model.SetWinningType.WINNING_POINTS_ABSOLUTE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pj.squashrestapp.dto.matchresulthelper.SetScoreHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for {@link SetScoreHelper} class. */
@DisplayName("Set Score Test")
class SetScoreHelperTest {

  @Test
  @DisplayName("Assert - ADV_OF_2_OR_1_AT_THE_END")
  void computeSetWinnerScoreTest_ADV_OF_2_OR_1_AT_THE_END() {
    assertAll(
        "Assert - ADV_OF_2_OR_1_AT_THE_END",
        () -> assertEquals(11, SetScoreHelper.computeWinnerScore(0, 11, ADV_OF_2_OR_1_AT_THE_END)),
        () -> assertEquals(11, SetScoreHelper.computeWinnerScore(6, 11, ADV_OF_2_OR_1_AT_THE_END)),
        () -> assertEquals(12, SetScoreHelper.computeWinnerScore(10, 11, ADV_OF_2_OR_1_AT_THE_END)),
        () -> assertEquals(10, SetScoreHelper.computeWinnerScore(9, 9, ADV_OF_2_OR_1_AT_THE_END)));
  }

  @Test
  @DisplayName("Assert - WINNING_POINTS_ABSOLUTE")
  void computeSetWinnerScoreTest_WINNING_POINTS_ABSOLUTE() {
    assertAll(
        "Assert - WINNING_POINTS_ABSOLUTE",
        () -> assertEquals(11, SetScoreHelper.computeWinnerScore(0, 11, WINNING_POINTS_ABSOLUTE)),
        () -> assertEquals(11, SetScoreHelper.computeWinnerScore(6, 11, WINNING_POINTS_ABSOLUTE)),
        () -> assertEquals(11, SetScoreHelper.computeWinnerScore(10, 11, WINNING_POINTS_ABSOLUTE)));
  }

  @Test
  @DisplayName("Assert - ADVANTAGE_OF_2_ABSOLUTE")
  void computeSetWinnerScoreTest_ADVANTAGE_OF_2_ABSOLUTE() {
    assertAll(
        "Assert - ADVANTAGE_OF_2_ABSOLUTE",
        () -> assertEquals(11, SetScoreHelper.computeWinnerScore(0, 11, ADV_OF_2_ABSOLUTE)),
        () -> assertEquals(11, SetScoreHelper.computeWinnerScore(6, 11, ADV_OF_2_ABSOLUTE)),
        () -> assertEquals(12, SetScoreHelper.computeWinnerScore(10, 11, ADV_OF_2_ABSOLUTE)),
        () -> assertEquals(14, SetScoreHelper.computeWinnerScore(12, 11, ADV_OF_2_ABSOLUTE)));
  }
}
