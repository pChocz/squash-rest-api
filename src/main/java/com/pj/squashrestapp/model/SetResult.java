package com.pj.squashrestapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "set_results")
@Getter
@Setter
@NoArgsConstructor
public class SetResult {

  private static final String REGEX = "(\\s*\\d{1,2})\\D+(\\d{1,2}\\s*)";
  private static final Pattern PATTERN = Pattern.compile(REGEX);

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

  @Column(name = "number")
  private int number;

  @Column(name = "first_player_score")
  private int firstPlayerScore;

  @Column(name = "second_player_score")
  private int secondPlayerScore;

  @ManyToOne
  @JoinColumn(name = "match_id", referencedColumnName = "id")
  private Match match;

  public SetResult(final int number, final Match match) {
    this.number = number;
    this.match = match;
  }

  public SetResult(final int number, final String setScore, final Match match) {
    this.number = number;

    final Matcher matcher = PATTERN.matcher(setScore);
    matcher.find();

    this.firstPlayerScore = Integer.parseInt(matcher.group(1).trim());
    this.secondPlayerScore = Integer.parseInt(matcher.group(2).trim());
    this.match = match;
  }

  @Override
  public String toString() {
    return firstPlayerScore + ":" + secondPlayerScore;
  }

  public Player getWinner() {
    return firstPlayerScore > secondPlayerScore
            ? match.getFirstPlayer()
            : match.getSecondPlayer();
  }

  public void replaceResult(final String setScore) {
    final Matcher matcher = PATTERN.matcher(setScore);
    matcher.find();

    this.firstPlayerScore = Integer.parseInt(matcher.group(1).trim());
    this.secondPlayerScore = Integer.parseInt(matcher.group(2).trim());
  }

  public void replaceResult(final SetResult setResult) {
    this.firstPlayerScore = setResult.firstPlayerScore;
    this.secondPlayerScore = setResult.secondPlayerScore;
  }

  public boolean hasBeenPlayed() {
    return firstPlayerScore + secondPlayerScore > 0;
  }

}
