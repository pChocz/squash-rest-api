package com.pj.squashrestapp.dto.scoreboard.headtohead;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.scoreboard.PlayersStatsScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.ScoreboardRow;
import com.pj.squashrestapp.util.RoundingUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

/** */
@Getter
@Setter
@NoArgsConstructor
public class HeadToHeadScoreboardRow implements ScoreboardRow {

    private PlayerDto player;

    private int pointsWon;
    private int pointsLost;
    private int pointsBalance;
    private BigDecimal pointsRatio;

    private int setsWon;
    private int setsLost;
    private int setsBalance;
    private BigDecimal setsRatio;

    private int matchesWon;
    private int matchesLost;
    private int matchesBalance;
    private BigDecimal matchesRatio;

    private int oneSetMatchesWon;
    private int oneSetMatchesLost;
    private BigDecimal oneSetMatchesRatio;

    private int twoSetsMatchesWon;
    private int twoSetsMatchesLost;
    private BigDecimal twoSetsMatchesRatio;

    private int threeSetsMatchesWon;
    private int threeSetsMatchesLost;
    private BigDecimal threeSetsMatchesRatio;

    private int fourSetsMatchesWon;
    private int fourSetsMatchesLost;
    private BigDecimal fourSetsMatchesRatio;

    private int fiveSetsMatchesWon;
    private int fiveSetsMatchesLost;
    private BigDecimal fiveSetsMatchesRatio;

    private int firstSetsWon;
    private int firstSetsLost;
    private BigDecimal firstSetsRatio;

    private int secondSetsWon;
    private int secondSetsLost;
    private BigDecimal secondSetsRatio;

    private int thirdSetsWon;
    private int thirdSetsLost;
    private BigDecimal thirdSetsRatio;

    private int fourthSetsWon;
    private int fourthSetsLost;
    private BigDecimal fourthSetsRatio;

    private int fifthSetsWon;
    private int fifthSetsLost;
    private BigDecimal fifthSetsRatio;

    public HeadToHeadScoreboardRow(
            final PlayersStatsScoreboardRow row,
            final Map<PlayerDto, Map<Integer, Integer>> splitPerSet,
            final Map<PlayerDto, Map<Integer, Integer>> splitPerMatch) {

        this.player = row.getPlayer();

        this.pointsWon = row.getPointsWon();
        this.pointsLost = row.getPointsLost();
        this.pointsRatio = RoundingUtil.round((float) 100 * pointsWon / (pointsWon + pointsLost), 1);

        this.setsWon = row.getSetsWon();
        this.setsLost = row.getSetsLost();
        this.setsRatio = RoundingUtil.round((float) 100 * setsWon / (setsWon + setsLost), 1);

        this.matchesWon = row.getMatchesWon();
        this.matchesLost = row.getMatchesLost();
        this.matchesRatio = RoundingUtil.round((float) 100 * matchesWon / (matchesWon + matchesLost), 1);

        final PlayerDto opponent = splitPerSet.keySet().stream()
                .filter(playerDto -> !playerDto.equals(this.player))
                .findFirst()
                .orElseThrow();

        // Sets

        this.firstSetsWon = splitPerSet.get(player).getOrDefault(1, 0);
        this.firstSetsLost = splitPerSet.get(opponent).getOrDefault(1, 0);
        this.firstSetsRatio = RoundingUtil.round((float) 100 * firstSetsWon / (firstSetsWon + firstSetsLost), 1);

        this.secondSetsWon = splitPerSet.get(player).getOrDefault(2, 0);
        this.secondSetsLost = splitPerSet.get(opponent).getOrDefault(2, 0);
        if (secondSetsWon + secondSetsLost > 0) {
            this.secondSetsRatio =
                    RoundingUtil.round((float) 100 * secondSetsWon / (secondSetsWon + secondSetsLost), 1);
        }

        this.thirdSetsWon = splitPerSet.get(player).getOrDefault(3, 0);
        this.thirdSetsLost = splitPerSet.get(opponent).getOrDefault(3, 0);
        if (thirdSetsWon + thirdSetsLost > 0) {
            this.thirdSetsRatio = RoundingUtil.round((float) 100 * thirdSetsWon / (thirdSetsWon + thirdSetsLost), 1);
        }

        this.fourthSetsWon = splitPerSet.get(player).getOrDefault(4, 0);
        this.fourthSetsLost = splitPerSet.get(opponent).getOrDefault(4, 0);
        if (fourthSetsWon + fourthSetsLost > 0) {
            this.fourthSetsRatio =
                    RoundingUtil.round((float) 100 * fourthSetsWon / (fourthSetsWon + fourthSetsLost), 1);
        }

        this.fifthSetsWon = splitPerSet.get(player).getOrDefault(5, 0);
        this.fifthSetsLost = splitPerSet.get(opponent).getOrDefault(5, 0);
        if (fifthSetsWon + fifthSetsLost > 0) {
            this.fifthSetsRatio = RoundingUtil.round((float) 100 * fifthSetsWon / (fifthSetsWon + fifthSetsLost), 1);
        }

        // Matches

        this.oneSetMatchesWon = splitPerMatch.get(player).getOrDefault(1, 0);
        this.oneSetMatchesLost = splitPerMatch.get(opponent).getOrDefault(1, 0);
        if (oneSetMatchesWon + oneSetMatchesLost > 0) {
            this.oneSetMatchesRatio =
                    RoundingUtil.round((float) 100 * oneSetMatchesWon / (oneSetMatchesWon + oneSetMatchesLost), 1);
        }

        this.twoSetsMatchesWon = splitPerMatch.get(player).getOrDefault(2, 0);
        this.twoSetsMatchesLost = splitPerMatch.get(opponent).getOrDefault(2, 0);
        if (twoSetsMatchesWon + twoSetsMatchesLost > 0) {
            this.twoSetsMatchesRatio =
                    RoundingUtil.round((float) 100 * twoSetsMatchesWon / (twoSetsMatchesWon + twoSetsMatchesLost), 1);
        }

        this.threeSetsMatchesWon = splitPerMatch.get(player).getOrDefault(3, 0);
        this.threeSetsMatchesLost = splitPerMatch.get(opponent).getOrDefault(3, 0);
        if (threeSetsMatchesWon + threeSetsMatchesLost > 0) {
            this.threeSetsMatchesRatio = RoundingUtil.round(
                    (float) 100 * threeSetsMatchesWon / (threeSetsMatchesWon + threeSetsMatchesLost), 1);
        }

        this.fourSetsMatchesWon = splitPerMatch.get(player).getOrDefault(4, 0);
        this.fourSetsMatchesLost = splitPerMatch.get(opponent).getOrDefault(4, 0);
        if (fourSetsMatchesWon + fourSetsMatchesLost > 0) {
            this.fourSetsMatchesRatio = RoundingUtil.round(
                    (float) 100 * fourSetsMatchesWon / (fourSetsMatchesWon + fourSetsMatchesLost), 1);
        }

        this.fiveSetsMatchesWon = splitPerMatch.get(player).getOrDefault(5, 0);
        this.fiveSetsMatchesLost = splitPerMatch.get(opponent).getOrDefault(5, 0);
        if (fiveSetsMatchesWon + fiveSetsMatchesLost > 0) {
            this.fiveSetsMatchesRatio = RoundingUtil.round(
                    (float) 100 * fiveSetsMatchesWon / (fiveSetsMatchesWon + fiveSetsMatchesLost), 1);
        }
    }

    public int getPointsBalance() {
        return getPointsWon() - getPointsLost();
    }

    public int getSetsBalance() {
        return getSetsWon() - getSetsLost();
    }

    public int getMatchesBalance() {
        return getMatchesWon() - getMatchesLost();
    }

    @Override
    public String toString() {
        return player.getUsername();
    }
}
