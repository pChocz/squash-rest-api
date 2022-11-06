package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.enums.AdditionalMatchType;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

/** */
@UtilityClass
public class FakeMatch {

    public AdditionalMatch createAdditional(
            final League league,
            final int seasonNumber,
            final Player firstPlayer,
            final Player secondPlayer,
            final LocalDate date,
            final AdditionalMatchType type) {

        final AdditionalMatch match = new AdditionalMatch(firstPlayer, secondPlayer, league);
        match.setDate(date);
        match.setType(type);
        match.setSeasonNumber(seasonNumber);

        switch (match.getMatchFormatType()) {
            case ONE_GAME -> {
                match.addSetResult(FakeSetResult.createAdditional(
                        1, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));
            }
            case BEST_OF_3 -> {
                match.addSetResult(FakeSetResult.createAdditional(
                        1, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));
                match.addSetResult(FakeSetResult.createAdditional(
                        2, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));

                if (isFinishedAdditionalMatch(match, 2)) {
                    match.addSetResult(FakeSetResult.createNullSetAdditional(3));
                } else {
                    match.addSetResult(FakeSetResult.createAdditional(
                            3, match.getTiebreakWinningType(), match.getTiebreakWinningPoints()));
                }
            }
            case BEST_OF_5 -> {
                match.addSetResult(FakeSetResult.createAdditional(
                        1, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));
                match.addSetResult(FakeSetResult.createAdditional(
                        2, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));
                match.addSetResult(FakeSetResult.createAdditional(
                        3, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));

                if (isFinishedAdditionalMatch(match, 3)) {
                    match.addSetResult(FakeSetResult.createNullSetAdditional(4));
                    match.addSetResult(FakeSetResult.createNullSetAdditional(5));
                    return match;
                }

                match.addSetResult(FakeSetResult.createAdditional(
                        4, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));

                if (isFinishedAdditionalMatch(match, 3)) {
                    match.addSetResult(FakeSetResult.createNullSetAdditional(5));
                    return match;
                }

                // otherwise it means that it's draw after 4 sets
                match.addSetResult(FakeSetResult.createAdditional(
                        5, match.getTiebreakWinningType(), match.getTiebreakWinningPoints()));
            }
        }

        return match;
    }

    public Match create(final Season season, final Player firstPlayer, final Player secondPlayer) {
        final Match match = new Match(firstPlayer, secondPlayer, season);

        switch (match.getMatchFormatType()) {
            case ONE_GAME -> {
                match.addSetResult(FakeSetResult.create(
                        1, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));
            }
            case BEST_OF_3 -> {
                match.addSetResult(FakeSetResult.create(
                        1, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));
                match.addSetResult(FakeSetResult.create(
                        2, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));

                if (isFinishedMatch(match, 2)) {
                    match.addSetResult(FakeSetResult.createNullSet(3));
                } else {
                    match.addSetResult(FakeSetResult.create(
                            3, match.getTiebreakWinningType(), match.getTiebreakWinningPoints()));
                }
            }
            case BEST_OF_5 -> {
                match.addSetResult(FakeSetResult.create(
                        1, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));
                match.addSetResult(FakeSetResult.create(
                        2, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));
                match.addSetResult(FakeSetResult.create(
                        3, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));

                if (isFinishedMatch(match, 3)) {
                    match.addSetResult(FakeSetResult.createNullSet(4));
                    match.addSetResult(FakeSetResult.createNullSet(5));
                    return match;
                }

                match.addSetResult(FakeSetResult.create(
                        4, match.getRegularSetWinningType(), match.getRegularSetWinningPoints()));

                if (isFinishedMatch(match, 3)) {
                    match.addSetResult(FakeSetResult.createNullSet(5));
                    return match;
                }

                // otherwise it means that it's draw after 4 sets
                match.addSetResult(
                        FakeSetResult.create(5, match.getTiebreakWinningType(), match.getTiebreakWinningPoints()));
            }
        }

        return match;
    }

    private boolean isFinishedMatch(final Match match, final int numberOfSetsToWin) {
        int firstPlayerWon = 0;
        int secondPlayerWon = 0;
        for (final SetResult setResult : match.getSetResults()) {
            if (setResult.getFirstPlayerScore() > setResult.getSecondPlayerScore()) {
                firstPlayerWon++;
            } else {
                secondPlayerWon++;
            }
        }
        return firstPlayerWon == numberOfSetsToWin || secondPlayerWon == numberOfSetsToWin;
    }

    private boolean isFinishedAdditionalMatch(final AdditionalMatch match, final int numberOfSetsToWin) {
        int firstPlayerWon = 0;
        int secondPlayerWon = 0;
        for (final AdditionalSetResult setResult : match.getSetResults()) {
            if (setResult.getFirstPlayerScore() > setResult.getSecondPlayerScore()) {
                firstPlayerWon++;
            } else {
                secondPlayerWon++;
            }
        }
        return firstPlayerWon == numberOfSetsToWin || secondPlayerWon == numberOfSetsToWin;
    }
}
