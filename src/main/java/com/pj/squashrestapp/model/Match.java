package com.pj.squashrestapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Entity
@Table(name = "matches")
@NamedEntityGraphs(
        @NamedEntityGraph(name = "match-detailed",
                attributeNodes = {
                        @NamedAttributeNode("setResults"),
                        @NamedAttributeNode("firstPlayer"),
                        @NamedAttributeNode("secondPlayer"),
                        @NamedAttributeNode("roundGroup")
                }))
@Getter
@NoArgsConstructor
public class Match {

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

  @OneToOne
  @JoinColumn(name = "first_player_id", referencedColumnName = "id")
  private Player firstPlayer;

  @OneToOne
  @JoinColumn(name = "second_player_id", referencedColumnName = "id")
  private Player secondPlayer;

  @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SetResult> setResults;

  private transient String _1stSetResult;
  private transient String _2ndSetResult;
  private transient String _3rdSetResult;

  @ManyToOne
  @JoinColumn(name = "round_group_id", referencedColumnName = "id")
  private RoundGroup roundGroup;

  public Match(final RoundGroup roundGroup, final Player firstPlayer, final Player secondPlayer, final boolean withAllEmptySets) {
    this.roundGroup = roundGroup;
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
    this.setResults = new ArrayList<>(3);
    this.setResults.add(new SetResult(1, this));
    this.setResults.add(new SetResult(2, this));
    this.setResults.add(new SetResult(3, this));
  }

  public Match(final RoundGroup roundGroup, final Player firstPlayer, final Player secondPlayer) {
    this.roundGroup = roundGroup;
    this.firstPlayer = firstPlayer;
    this.secondPlayer = secondPlayer;
  }

  public void replaceSetResults(final String firstSetResult, final String secondSetResult, final String thirdSetResult) {
    replaceFirstAndSecondSet(firstSetResult, secondSetResult);

    final int setCount = this.setResults.size();
    if (setCount == 3) {
      final SetResult setThreeResult = this.setResults.get(2);
      setThreeResult.replaceResult(thirdSetResult);
    } else {
      final SetResult setThreeResult = new SetResult(3, thirdSetResult, this);
      this.setResults.add(setThreeResult);
    }
  }

  private void replaceFirstAndSecondSet(final String firstSetResult, final String secondSetResult) {
    final SetResult setOneResult = this.setResults.get(0);
    setOneResult.replaceResult(firstSetResult);

    final SetResult setTwoResult = this.setResults.get(1);
    setTwoResult.replaceResult(secondSetResult);
  }

  public void replaceSetResults(final String firstSetResult, final String secondSetResult) {
    replaceFirstAndSecondSet(firstSetResult, secondSetResult);

    final int setCount = this.setResults.size();
    if (setCount == 3) {
      this.setResults.remove(2);
    }
  }

  public int setsPlayed() {
    return (int) this
            .getSetResults()
            .stream()
            .filter(setResult -> setResult.getFirstPlayerScore() + setResult.getSecondPlayerScore() > 0)
            .count();
  }

//  public Player getWinner() {
//    final int[] result = new int[2];
//
//    for (final SetResult setResult : this.getSetResults()) {
//      if (setResult.getFirstPlayerScore() > setResult.getSecondPlayerScore()) {
//        result[0]++;
//      } else if (setResult.getFirstPlayerScore() < setResult.getSecondPlayerScore()) {
//        result[1]++;
//      }
//    }
//
//    if (result[0] == result[1] || result[0] + result[1] < 2) {
//      return null;
//
//    } else if (result[0] > result[1]) {
//      return this.firstPlayer;
//
//    } else if (result[0] < result[1]) {
//      return this.secondPlayer;
//    }
//
//    // should never happen
//    return null;
//  }

  public Player getWinnerOfSet(final int setNumber) {
    final int score1stPlayer = getSetResults().get(setNumber - 1).getFirstPlayerScore();
    final int score2ndPlayer = getSetResults().get(setNumber - 1).getSecondPlayerScore();
    return score1stPlayer > score2ndPlayer
            ? firstPlayer
            : score1stPlayer < score2ndPlayer
            ? secondPlayer
            : null;
  }

  @Override
  public String toString() {
    return "[" + dbId() + "] " + firstPlayer + " vs. " + secondPlayer + " : " + setResults;
  }

  private String dbId() {
    return id == null
            ? "no id"
            : String.valueOf(id);
  }

}
