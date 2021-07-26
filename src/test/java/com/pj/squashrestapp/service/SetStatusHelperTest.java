package com.pj.squashrestapp.service;

import static com.pj.squashrestapp.dto.matchresulthelper.SetStatus.EMPTY;
import static com.pj.squashrestapp.dto.matchresulthelper.SetStatus.ERROR;
import static com.pj.squashrestapp.dto.matchresulthelper.SetStatus.FIRST_PLAYER_WINS;
import static com.pj.squashrestapp.dto.matchresulthelper.SetStatus.IN_PROGRESS;
import static com.pj.squashrestapp.dto.matchresulthelper.SetStatus.SECOND_PLAYER_WINS;
import static com.pj.squashrestapp.model.SetWinningType.ADV_OF_2_ABSOLUTE;
import static com.pj.squashrestapp.model.SetWinningType.ADV_OF_2_OR_1_AT_THE_END;
import static com.pj.squashrestapp.model.SetWinningType.WINNING_POINTS_ABSOLUTE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pj.squashrestapp.dto.match.SetDto;
import com.pj.squashrestapp.dto.matchresulthelper.SetStatusHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for {@link SetStatusHelper} class. */
// @Disabled
@DisplayName("Set Status Test")
class SetStatusHelperTest {

  @Test
  @DisplayName("Assert - Set is FINISHED")
  void checkSetStatus_FINISHED() {
    assertAll(
        "Assert - Set is FINISHED - ADV_OF_2_OR_1_AT_THE_END",
        () ->
            assertEquals(
                FIRST_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("11:8"), 11, ADV_OF_2_OR_1_AT_THE_END)),
        () ->
            assertEquals(
                FIRST_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("12:10"), 11, ADV_OF_2_OR_1_AT_THE_END)),
        () ->
            assertEquals(
                SECOND_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("11:12"), 11, ADV_OF_2_OR_1_AT_THE_END)),
        () ->
            assertEquals(
                SECOND_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("4:11"), 11, ADV_OF_2_OR_1_AT_THE_END)));

    assertAll(
        "Assert - Set is FINISHED - ADV_OF_2_ABSOLUTE",
        () ->
            assertEquals(
                FIRST_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("11:8"), 11, ADV_OF_2_ABSOLUTE)),
        () ->
            assertEquals(
                FIRST_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("13:11"), 11, ADV_OF_2_ABSOLUTE)),
        () ->
            assertEquals(
                SECOND_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("12:14"), 11, ADV_OF_2_ABSOLUTE)),
        () ->
            assertEquals(
                SECOND_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("4:11"), 11, ADV_OF_2_ABSOLUTE)));

    assertAll(
        "Assert - Set is FINISHED - WINNING_POINTS_ABSOLUTE",
        () ->
            assertEquals(
                FIRST_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("11:8"), 11, WINNING_POINTS_ABSOLUTE)),
        () ->
            assertEquals(
                FIRST_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("11:10"), 11, WINNING_POINTS_ABSOLUTE)),
        () ->
            assertEquals(
                SECOND_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("3:11"), 11, WINNING_POINTS_ABSOLUTE)),
        () ->
            assertEquals(
                SECOND_PLAYER_WINS,
                SetStatusHelper.checkStatus(new SetDto("9:11"), 11, WINNING_POINTS_ABSOLUTE)));
  }

  @Test
  @DisplayName("Assert - Set is EMPTY")
  void checkSetStatus_EMPTY() {
    assertAll(
        "Assert - Set is EMPTY",
        () ->
            assertEquals(
                EMPTY,
                SetStatusHelper.checkStatus(new SetDto("n:n"), 11, ADV_OF_2_OR_1_AT_THE_END)),
        () ->
            assertEquals(
                EMPTY, SetStatusHelper.checkStatus(new SetDto("n:n"), 11, ADV_OF_2_ABSOLUTE)),
        () ->
            assertEquals(
                EMPTY,
                SetStatusHelper.checkStatus(new SetDto("n:n"), 11, WINNING_POINTS_ABSOLUTE)));
  }

  @Test
  @DisplayName("Assert - Set is IN PROGRESS")
  void checkSetStatus_IN_PROGRESS() {
    assertAll(
        "Assert - Set is IN PROGRESS - ADV_OF_2_OR_1_AT_THE_END",
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("10:8"), 11, ADV_OF_2_OR_1_AT_THE_END)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("11:10"), 11, ADV_OF_2_OR_1_AT_THE_END)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("4:10"), 11, ADV_OF_2_OR_1_AT_THE_END)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("0:5"), 11, ADV_OF_2_OR_1_AT_THE_END)));

    assertAll(
        "Assert - Set is IN PROGRESS - ADV_OF_2_ABSOLUTE",
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("11:10"), 11, ADV_OF_2_ABSOLUTE)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("13:12"), 11, ADV_OF_2_ABSOLUTE)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("4:10"), 11, ADV_OF_2_ABSOLUTE)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("11:11"), 11, ADV_OF_2_ABSOLUTE)));

    assertAll(
        "Assert - Set is IN PROGRESS - WINNING_POINTS_ABSOLUTE",
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("9:8"), 11, WINNING_POINTS_ABSOLUTE)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("10:10"), 11, WINNING_POINTS_ABSOLUTE)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("3:9"), 11, WINNING_POINTS_ABSOLUTE)),
        () ->
            assertEquals(
                IN_PROGRESS,
                SetStatusHelper.checkStatus(new SetDto("0:9"), 11, WINNING_POINTS_ABSOLUTE)));
  }

  @Test
  @DisplayName("Assert - Set has ERROR")
  void checkSetStatus_ERROR() {
    assertAll(
        "Assert - Set has ERROR - ADV_OF_2_OR_1_AT_THE_END",
        () ->
            assertEquals(
                ERROR,
                SetStatusHelper.checkStatus(new SetDto("12:7"), 11, ADV_OF_2_OR_1_AT_THE_END)),
        () ->
            assertEquals(
                ERROR,
                SetStatusHelper.checkStatus(new SetDto("0:12"), 11, ADV_OF_2_OR_1_AT_THE_END)));

    assertAll(
        "Assert - Set has ERROR - ADV_OF_2_ABSOLUTE",
        () ->
            assertEquals(
                ERROR, SetStatusHelper.checkStatus(new SetDto("12:9"), 11, ADV_OF_2_ABSOLUTE)),
        () ->
            assertEquals(
                ERROR, SetStatusHelper.checkStatus(new SetDto("13:8"), 11, ADV_OF_2_ABSOLUTE)));

    assertAll(
        "Assert - Set has ERROR - WINNING_POINTS_ABSOLUTE",
        () ->
            assertEquals(
                ERROR,
                SetStatusHelper.checkStatus(new SetDto("12:8"), 11, WINNING_POINTS_ABSOLUTE)),
        () ->
            assertEquals(
                ERROR,
                SetStatusHelper.checkStatus(new SetDto("10:12"), 11, WINNING_POINTS_ABSOLUTE)));
  }
}
