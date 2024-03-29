package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatusHelper;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.pj.squashrestapp.dto.matchresulthelper.MatchStatus.EMPTY;
import static com.pj.squashrestapp.dto.matchresulthelper.MatchStatus.ERROR;
import static com.pj.squashrestapp.dto.matchresulthelper.MatchStatus.FINISHED;
import static com.pj.squashrestapp.dto.matchresulthelper.MatchStatus.IN_PROGRESS;
import static com.pj.squashrestapp.model.enums.SetWinningType.ADV_OF_2_ABSOLUTE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link MatchStatusHelper} class.
 *
 * <p>note that 'n:n' means 'NULL:NULL'.
 */
@DisplayName("Match Status Test - Advantage of 2 absolute")
class MatchStatusAdvantageOf2AbsoluteHelperTest {

    @Test
    @DisplayName("Assert - Match is FINISHED")
    void checkMatchStatus_FINISHED() {

        assertAll(
                "Assert - Match is FINISHED - One Game",
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2OneGameMatch("11:9")),
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2OneGameMatch("10:12")));

        assertAll(
                "Assert - Match is FINISHED - Best of 3",
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2BestOf3Match("11:9", "11:6", "n:n")),
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2BestOf3Match("10:12", "9:11", "n:n")),
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2BestOf3Match("11:9", "6:11", "10:8")),
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2BestOf3Match("11:9", "6:11", "5:9")),
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2BestOf3Match("13:11", "13:11", "n:n")));

        assertAll(
                "Assert - Match is FINISHED - Best of 5",
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2BestOf5Match("11:9", "11:6", "12:10", "n:n", "n:n")),
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2BestOf5Match("11:6", "11:9", "5:11", "11:8", "n:n")),
                () -> assertEquals(FINISHED, getStatusForAbsDiffOf2BestOf5Match("5:11", "11:1", "11:6", "7:11", "9:6")),
                () -> assertEquals(
                        FINISHED, getStatusForAbsDiffOf2BestOf5Match("12:10", "13:11", "8:11", "6:11", "10:8")));
    }

    @Test
    @DisplayName("Assert - Match is EMPTY")
    void checkMatchStatus_EMPTY() {
        assertAll(
                "Assert - Match is EMPTY",
                () -> assertEquals(EMPTY, getStatusForAbsDiffOf2OneGameMatch("n:n")),
                () -> assertEquals(EMPTY, getStatusForAbsDiffOf2BestOf3Match("n:n", "n:n", "n:n")),
                () -> assertEquals(EMPTY, getStatusForAbsDiffOf2BestOf5Match("n:n", "n:n", "n:n", "n:n", "n:n")));
    }

    @Test
    @DisplayName("Assert - Match has ERROR")
    void checkMatchStatus_ERROR() {
        assertAll(
                "Assert - Match has ERROR - One game",
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2OneGameMatch("10:14")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2OneGameMatch("14:11")));

        assertAll(
                "Assert - Match has ERROR - Best of 3",
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf3Match("14:10", "11:9", "9:5")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf3Match("11:7", "11:9", "9:5")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf3Match("5:6", "n:n", "9:5")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf3Match("n:n", "11:9", "9:5")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf3Match("14:12", "11:9", "11:8")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf3Match("1:11", "10:12", "5:9")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf3Match("10:11", "11:9", "9:5")));

        assertAll(
                "Assert - Match has ERROR - Best of 5",
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf5Match("10:11", "11:9", "9:5", "n:n", "n:n")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf5Match("n:n", "11:9", "9:5", "11:8", "n:n")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf5Match("12:10", "11:9", "11:5", "11:8", "n:n")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf5Match("n:n", "11:9", "11:6", "11:8", "n:n")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf5Match("n:n", "n:n", "9:5", "n:n", "n:n")),
                () -> assertEquals(ERROR, getStatusForAbsDiffOf2BestOf5Match("14:10", "11:9", "9:5", "n:n", "n:n")));
    }

    @Test
    @DisplayName("Assert - Match is IN PROGRESS")
    void checkMatchStatus_IN_PROGRESS() {
        assertAll(
                "Assert - Match is IN PROGRESS - One game",
                () -> assertEquals(IN_PROGRESS, getStatusForAbsDiffOf2OneGameMatch("6:7")),
                () -> assertEquals(IN_PROGRESS, getStatusForAbsDiffOf2OneGameMatch("1:0")));

        assertAll(
                "Assert - Match is IN PROGRESS - Best of 3",
                () -> assertEquals(IN_PROGRESS, getStatusForAbsDiffOf2BestOf3Match("11:9", "n:n", "n:n")),
                () -> assertEquals(IN_PROGRESS, getStatusForAbsDiffOf2BestOf3Match("11:7", "8:11", "n:n")),
                () -> assertEquals(IN_PROGRESS, getStatusForAbsDiffOf2BestOf3Match("11:7", "8:11", "5:2")),
                () -> assertEquals(IN_PROGRESS, getStatusForAbsDiffOf2BestOf3Match("11:5", "1:8", "n:n")));

        assertAll(
                "Assert - Match is IN PROGRESS - Best of 5",
                () -> assertEquals(IN_PROGRESS, getStatusForAbsDiffOf2BestOf5Match("11:9", "n:n", "n:n", "n:n", "n:n")),
                () -> assertEquals(
                        IN_PROGRESS, getStatusForAbsDiffOf2BestOf5Match("11:9", "11:5", "n:n", "n:n", "n:n")),
                () -> assertEquals(
                        IN_PROGRESS, getStatusForAbsDiffOf2BestOf5Match("11:9", "11:5", "6:11", "6:11", "n:n")),
                () -> assertEquals(
                        IN_PROGRESS, getStatusForAbsDiffOf2BestOf5Match("11:9", "12:10", "6:11", "6:11", "n:n")),
                () -> assertEquals(
                        IN_PROGRESS, getStatusForAbsDiffOf2BestOf5Match("11:9", "12:10", "6:11", "6:11", "5:2")));
    }

    private MatchStatus getStatusForAbsDiffOf2OneGameMatch(final String... setResults) {
        return new MatchSimpleDto(MatchFormatType.ONE_GAME, ADV_OF_2_ABSOLUTE, ADV_OF_2_ABSOLUTE, 11, 11, setResults)
                .getStatus();
    }

    private MatchStatus getStatusForAbsDiffOf2BestOf3Match(final String... setResults) {
        return new MatchSimpleDto(MatchFormatType.BEST_OF_3, ADV_OF_2_ABSOLUTE, ADV_OF_2_ABSOLUTE, 11, 9, setResults)
                .getStatus();
    }

    private MatchStatus getStatusForAbsDiffOf2BestOf5Match(final String... setResults) {
        return new MatchSimpleDto(MatchFormatType.BEST_OF_5, ADV_OF_2_ABSOLUTE, ADV_OF_2_ABSOLUTE, 11, 9, setResults)
                .getStatus();
    }
}
