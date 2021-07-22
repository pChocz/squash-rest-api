package com.pj.squashrestapp.dto.match;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetResult;
import lombok.Getter;
import lombok.Setter;

/** */
@Getter
public class SetDto {

  @Setter
  private int setNumber;
  private final Integer firstPlayerScore;
  private final Integer secondPlayerScore;
  private final MatchFormatType matchFormatType;

  public SetDto(final SetResult setResult, final MatchFormatType matchFormatType) {
    this.setNumber = setResult.getNumber();
    this.firstPlayerScore = setResult.getFirstPlayerScore();
    this.secondPlayerScore = setResult.getSecondPlayerScore();
    this.matchFormatType = matchFormatType;
  }

  public SetDto(final AdditionalSetResult setResult, final MatchFormatType matchFormatType) {
    this.setNumber = setResult.getNumber();
    this.firstPlayerScore = setResult.getFirstPlayerScore();
    this.secondPlayerScore = setResult.getSecondPlayerScore();
    this.matchFormatType = matchFormatType;
  }

  public SetDto(final String setResultAsString) {
    this.setNumber = -1;
    this.matchFormatType = null;
    final String[] scoreSplitted = setResultAsString.split(":");
    final String firstPlayerScoreAsString = scoreSplitted[0].trim();
    final String secondPlayerScoreAsString = scoreSplitted[1].trim();

    Integer firstPlayerScore;
    try {
      firstPlayerScore = Integer.parseInt(firstPlayerScoreAsString);
    } catch (final NumberFormatException e) {
      firstPlayerScore = null;
    }

    Integer secondPlayerScore;
    try {
      secondPlayerScore = Integer.parseInt(secondPlayerScoreAsString);
    } catch (final NumberFormatException e) {
      secondPlayerScore = null;
    }

    this.firstPlayerScore = firstPlayerScore;
    this.secondPlayerScore = secondPlayerScore;
  }

  @JsonIgnore
  public boolean isNonEmpty() {
    return !isEmpty();
  }

  @JsonIgnore
  public boolean isEmpty() {
    return firstPlayerScore == null && secondPlayerScore == null;
  }

  @JsonIgnore
  public boolean isTieBreak() {
    if (this.matchFormatType == MatchFormatType.ONE_GAME) {
      return false;
    } else {
      final int tieBreakSetNumber = this.matchFormatType.getMaxNumberOfSets();
      return setNumber == tieBreakSetNumber;
    }
  }

  @Override
  public String toString() {
    return "set " + setNumber + " -> " + firstPlayerScore + ":" + secondPlayerScore;
  }

  @JsonIgnore
  public Integer getFirstPlayerScoreNullSafe() {
    return firstPlayerScore == null ? 0 : firstPlayerScore;
  }

  @JsonIgnore
  public Integer getSecondPlayerScoreNullSafe() {
    return secondPlayerScore == null ? 0 : secondPlayerScore;
  }
}
