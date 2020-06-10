package com.pj.squashrestapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "season_results")
@Getter
@Setter
@NoArgsConstructor
public class SeasonResult implements Comparable<SeasonResult> {

  @Transient
  private int numberOfRoundsThatCount = 8;
  @Transient
  private int numberOfRoundsPlayed = 10;

  @Id
  @Column(name = "id",
          nullable = false,
          updatable = false)
  @GeneratedValue(
          strategy = GenerationType.AUTO,
          generator = "native")
  @GenericGenerator(
          name = "native",
          strategy = "native")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_id", referencedColumnName = "id")
  private Player player;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id", referencedColumnName = "id")
  private Season season;

  @OneToMany(mappedBy = "seasonResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<RoundResult> roundResults;

  @Column(name = "bonus_points")
  private int bonusPoints;

  public SeasonResult(final Player player, final Season season) {
    this.player = player;
    this.season = season;
  }

  public void initializeEmptyPointsForRounds() {
    this.roundResults = new ArrayList<>();
    for (int i = 1; i <= numberOfRoundsPlayed; i++) {
      this.roundResults.add(new RoundResult(i, this));
    }
    this.bonusPoints = 0;
  }

  public int calculateAverage() {
    return this.calculateAttendedRounds() == 0
            ? 0
            : (int) Math.round((double) calculateTotalPointsWithoutBonus() / (double) calculateAttendedRounds());
  }

  public int calculateAttendedRounds() {
    return (int) this
            .roundResults
            .stream()
            .filter(roundResult -> roundResult.getPoints() > 0)
            .count();
  }

  public int calculateTotalPointsWithoutBonus() {
    return this
            .roundResults
            .stream()
            .mapToInt(RoundResult::getPoints)
            .sum();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(player);
    for (final RoundResult roundResult : roundResults) {
      builder.append("\t-\t" + roundResult.getPoints());
    }
    builder.append("\nTOTAL     = " + calculateTotalPoints());
    builder.append("\nBEST " + numberOfRoundsThatCount + "/" + numberOfRoundsPlayed + " = " + calculatePointsThatCount());
    builder.append("\n\n");
    return builder.toString();
  }

  public int calculateTotalPoints() {
    return calculateTotalPointsWithoutBonus() + this.bonusPoints;
  }

  public int calculatePointsThatCount() {
    numberOfRoundsPlayed = season.getRounds().size();
    if (numberOfRoundsPlayed <= 4) {
      numberOfRoundsThatCount = numberOfRoundsPlayed;
    } else if (numberOfRoundsPlayed <= 8) {
      numberOfRoundsThatCount = numberOfRoundsPlayed - 1;
    } else {
      numberOfRoundsThatCount = numberOfRoundsPlayed - 2;
    }

    return this
            .roundResults
            .stream()
            .map(RoundResult::getPoints)
            .sorted(Comparator.reverseOrder())
            .limit(numberOfRoundsThatCount)
            .mapToInt(points -> points)
            .sum()
            + this.bonusPoints;
  }

  @Override
  public int compareTo(final SeasonResult that) {
    int result = Integer.compare(this.calculatePointsThatCount(), that.calculatePointsThatCount());
    if (result == 0) {
      result = Integer.compare(this.calculateTotalPoints(), that.calculateTotalPoints());
    }
    return -result;
  }

}
