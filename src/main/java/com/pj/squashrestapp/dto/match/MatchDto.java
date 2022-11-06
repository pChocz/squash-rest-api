package com.pj.squashrestapp.dto.match;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;

import java.time.LocalDate;
import java.util.List;

/** */
public interface MatchDto {

    PlayerDto getFirstPlayer();

    PlayerDto getSecondPlayer();

    PlayerDto getWinner();

    LocalDate getDate();

    List<SetDto> getSets();

    MatchFormatType getMatchFormatType();

    SetWinningType getRegularSetWinningType();

    int getRegularSetWinningPoints();

    SetWinningType getTieBreakWinningType();

    int getTieBreakWinningPoints();

    MatchStatus getStatus();
    boolean checkFinished();
    String getFootageLink();
}
