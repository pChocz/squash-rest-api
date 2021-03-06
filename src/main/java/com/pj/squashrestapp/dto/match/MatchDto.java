package com.pj.squashrestapp.dto.match;

import com.pj.squashrestapp.dto.PlayerDto;
import java.time.LocalDate;
import java.util.List;

/** */
public interface MatchDto {

  PlayerDto getFirstPlayer();

  PlayerDto getSecondPlayer();

  LocalDate getDate();

  List<SetDto> getSets();
}
