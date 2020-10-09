package com.pj.squashrestapp.util;

import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.ScoreboardRow;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

/**
 *
 */
@UtilityClass
public class LogUtil {

  public String extractPlayersCommaSeparated(final Scoreboard scoreboard) {
    return scoreboard
            .getScoreboardRows()
            .stream()
            .map(ScoreboardRow::getPlayer)
            .map(PlayerDto::getUsername)
            .collect(Collectors.joining(", ", "[", "]"));
  }

}
