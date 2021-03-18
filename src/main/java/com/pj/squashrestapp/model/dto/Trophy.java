package com.pj.squashrestapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Trophy {

  LEAGUE_1ST(false),
  LEAGUE_2ND(false),
  LEAGUE_3RD(false),

  CUP_1ST(false),
  CUP_2ND(false),
  CUP_3RD(false),

  SUPER_CUP(false),
  PRETENDERS_CUP(false),

  COVID(true),
  ALL_ROUNDS_ATTENDEE(true),
  ;

  @Getter
  private final boolean allowMultiple;

}
