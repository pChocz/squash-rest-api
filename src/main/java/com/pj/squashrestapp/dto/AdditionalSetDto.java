package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.AdditonalSetResult;
import com.pj.squashrestapp.model.SetResult;
import lombok.Getter;

/**
 *
 */
@Getter
public class AdditionalSetDto {

  private final int setNumber;
  private final Integer firstPlayerScore;
  private final Integer secondPlayerScore;

  public AdditionalSetDto(final AdditonalSetResult setResult) {
    this.setNumber = setResult.getNumber();
    this.firstPlayerScore = setResult.getFirstPlayerScore();
    this.secondPlayerScore = setResult.getSecondPlayerScore();
  }

}
