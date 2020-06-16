package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pj.squashrestapp.model.SetResult;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 *
 */
@Getter
@JsonInclude(NON_NULL)
public class SetDto {

  private final int setNumber;
  private final int firstPlayerScore;
  private final int secondPlayerScore;

  public SetDto(final SetResult setResult) {
    this.setNumber = setResult.getNumber();
    this.firstPlayerScore = setResult.getFirstPlayerScore();
    this.secondPlayerScore = setResult.getSecondPlayerScore();
  }

  @JsonIgnore
  public boolean isNotEmpty() {
    return !isEmpty();
  }

  @JsonIgnore
  public boolean isEmpty() {
    return firstPlayerScore == 0 && secondPlayerScore == 0;
  }

  @JsonIgnore
  public boolean isRegularSet() {
    return !isTieBreak();
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

}
