package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.AdditonalSetResult;
import com.pj.squashrestapp.model.SetResult;
import lombok.Getter;

/**
 *
 */
@Getter
public class SetDto {

  private final int setNumber;
  private final Integer firstPlayerScore;
  private final Integer secondPlayerScore;

  public SetDto(final SetResult setResult) {
    this.setNumber = setResult.getNumber();
    this.firstPlayerScore = setResult.getFirstPlayerScore();
    this.secondPlayerScore = setResult.getSecondPlayerScore();
  }

  public SetDto(final AdditonalSetResult setResult) {
    this.setNumber = setResult.getNumber();
    this.firstPlayerScore = setResult.getFirstPlayerScore();
    this.secondPlayerScore = setResult.getSecondPlayerScore();
  }

  @JsonIgnore
  public boolean isNonEmpty() {
    return !isEmpty();
  }

  @JsonIgnore
  public boolean isEmpty() {
    return firstPlayerScore == null
           && secondPlayerScore == null;
  }

  @JsonIgnore
  public boolean isTieBreak() {
    final int greaterScore = getGreaterScore();
    return greaterScore == 9;
  }

  private int getGreaterScore() {
    return firstPlayerScore > secondPlayerScore
            ? firstPlayerScore
            : firstPlayerScore < secondPlayerScore
            ? secondPlayerScore
            : 0;
  }

  @Override
  public String toString() {
    return "set " + setNumber + " -> " + firstPlayerScore + ":" + secondPlayerScore;
  }

  @JsonIgnore
  public Integer getFirstPlayerScoreNullSafe() {
    return firstPlayerScore == null
            ? 0
            : firstPlayerScore;
  }

  @JsonIgnore
  public Integer getSecondPlayerScoreNullSafe() {
    return secondPlayerScore == null
            ? 0
            : secondPlayerScore;
  }

}
