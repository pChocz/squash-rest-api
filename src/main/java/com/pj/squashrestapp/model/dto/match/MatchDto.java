package com.pj.squashrestapp.model.dto.match;

import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.SetDto;

import java.time.LocalDate;
import java.util.List;

/**
 *
 */
public interface MatchDto {

  PlayerDto getFirstPlayer();

  PlayerDto getSecondPlayer();

  LocalDate getRoundDate();

  List<SetDto> getSets();

}
