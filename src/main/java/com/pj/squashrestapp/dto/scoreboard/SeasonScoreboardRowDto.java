package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.dto.LostBallsAggregatedForSeason;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.util.RoundingUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class SeasonScoreboardRowDto implements Comparable<SeasonScoreboardRowDto> {

    @EqualsAndHashCode.Include
    private PlayerDto player;

    private Map<Integer, RoundAndGroupPosition> roundNumberToXpMapAll;
    private Map<Integer, Integer> roundNumberToXpMapPretenders;
    private int bonusPoints;
    private int lostBalls;
    private BigDecimal average;
    private int attendices;
    private int totalPoints;
    private int countedPoints;
    private int countedPointsPretenders;

    private int pointsWon;
    private int pointsLost;
    private int pointsBalance;

    private int setsWon;
    private int setsLost;
    private int setsBalance;

    private int matchesWon;
    private int matchesLost;
    private int matchesBalance;

    public SeasonScoreboardRowDto(
            final PlayerDto player,
            final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason,
            final LostBallsAggregatedForSeason lostBallsAggregatedForSeason) {
        this.player = player;
        this.roundNumberToXpMapAll = new HashMap<>();
        this.roundNumberToXpMapPretenders = new HashMap<>();

        final UUID currentPlayerUuid = player.getUuid();
        this.bonusPoints = bonusPointsAggregatedForSeason.forPlayer(currentPlayerUuid);
        this.lostBalls = lostBallsAggregatedForSeason.forPlayer(currentPlayerUuid);
    }

    public static double getAverageAsDouble(final SeasonScoreboardRowDto seasonScoreboardRowDto) {
        return (double) seasonScoreboardRowDto.totalPoints / seasonScoreboardRowDto.attendices;
    }

    public void addXpForRound(final int roundNumber, final RoundAndGroupPosition roundAndGroupPosition) {
        this.roundNumberToXpMapAll.put(roundNumber, roundAndGroupPosition);
    }

    public void addXpForRoundPretendents(final int roundNumber, final Integer xpEarned) {
        this.roundNumberToXpMapPretenders.put(roundNumber, xpEarned);
    }

    public void calculateFinishedRow(final int countedRounds) {
        final int totalPointsForRounds = getTotalPointsForRounds(roundNumberToXpMapAll);
        this.totalPoints = totalPointsForRounds + bonusPoints;

        final int countedPointsForRounds = getPointsForNumberOfRoundsComplex(roundNumberToXpMapAll, countedRounds);
        this.countedPointsPretenders = getPointsForNumberOfRoundsSimple(roundNumberToXpMapPretenders, countedRounds);

        this.countedPoints = countedPointsForRounds + bonusPoints;

        this.attendices = roundNumberToXpMapAll.size();
        this.average = RoundingUtil.round((float) totalPoints / attendices, 1);
    }

    private int getTotalPointsForRounds(final Map<Integer, RoundAndGroupPosition> roundNumberToXpMap) {
        return roundNumberToXpMap.values().stream()
                .mapToInt(RoundAndGroupPosition::getXpPoints)
                .sum();
    }

    private int getPointsForNumberOfRoundsSimple(
            final Map<Integer, Integer> roundNumberToXpMap, final int numberOfRounds) {
        return roundNumberToXpMap.values().stream()
                .sorted(Comparator.reverseOrder())
                .limit(numberOfRounds)
                .mapToInt(points -> points)
                .sum();
    }

    private int getPointsForNumberOfRoundsComplex(
            final Map<Integer, RoundAndGroupPosition> roundNumberToXpMap, final int numberOfRounds) {
        return roundNumberToXpMap.values().stream()
                .sorted(Comparator.reverseOrder())
                .limit(numberOfRounds)
                .mapToInt(RoundAndGroupPosition::getXpPoints)
                .sum();
    }

    @Override
    public int compareTo(final SeasonScoreboardRowDto that) {
        return Comparator.comparingInt(SeasonScoreboardRowDto::getCountedPoints)
                .thenComparingInt(SeasonScoreboardRowDto::getTotalPoints)
                .thenComparingDouble(SeasonScoreboardRowDto::getAverageAsDouble)
                .reversed()
                .compare(this, that);
    }

    public void addScoreboardRow(final RoundGroupScoreboardRow scoreboardRow) {
        this.pointsWon += scoreboardRow.getPointsWon();
        this.pointsLost += scoreboardRow.getPointsLost();
        this.pointsBalance += scoreboardRow.getPointsBalance();

        this.setsWon += scoreboardRow.getSetsWon();
        this.setsLost += scoreboardRow.getSetsLost();
        this.setsBalance += scoreboardRow.getSetsBalance();

        this.matchesWon += scoreboardRow.getMatchesWon();
        this.matchesLost += scoreboardRow.getMatchesLost();
        this.matchesBalance += scoreboardRow.getMatchesBalance();
    }
}
