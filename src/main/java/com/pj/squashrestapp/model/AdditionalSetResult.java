package com.pj.squashrestapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Comparator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "additional_set_results")
@Getter
@NoArgsConstructor
public class AdditionalSetResult implements Comparable<AdditionalSetResult> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(name = "number")
  private int number;

  @Setter
  @Column(name = "first_player_score")
  private Integer firstPlayerScore;

  @Setter
  @Column(name = "second_player_score")
  private Integer secondPlayerScore;

  @JsonIgnore
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_id")
  private AdditionalMatch match;

  public AdditionalSetResult(
      final int number, final Integer firstPlayerScore, final Integer secondPlayerScore) {
    this.number = number;
    this.firstPlayerScore = firstPlayerScore;
    this.secondPlayerScore = secondPlayerScore;
  }

  @Override
  public String toString() {
    return firstPlayerScore + ":" + secondPlayerScore;
  }

  @Override
  public int compareTo(final AdditionalSetResult that) {
    return Comparator.comparingInt(AdditionalSetResult::getNumber).compare(this, that);
  }

  public boolean nonNull() {
    return this.getFirstPlayerScore() != null && this.getSecondPlayerScore() != null;
  }
}
