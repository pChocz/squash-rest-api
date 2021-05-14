package com.pj.squashrestapp.dto.scoreboard.headtohead;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.MatchDto;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 */
@Getter
public class HeadToHeadChartData {

  private final Object[][] array;

  public HeadToHeadChartData(final Map<MatchDto, PlayerDto> matchWinnersMap, final PlayerDto statsWinner) {
    this.array = new Object[matchWinnersMap.size()][5];

    final ListIterator<MatchDto> iterator = new ArrayList<>(matchWinnersMap.keySet()).listIterator(matchWinnersMap.size());
    int i = 0;
    while (iterator.hasPrevious()) {
      final MatchDto match = iterator.previous();
      final PlayerDto matchWinner = matchWinnersMap.get(match);
      final boolean statsWinnerWon = matchWinner.equals(statsWinner);
      final int start = statsWinnerWon ? 0 : 1;
      final int end = statsWinnerWon ? 1 : 0;

      final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(GeneralUtil.DATE_FORMAT);
      final String date = match.getDate().format(formatter);

      this.array[i][0] = date;
      this.array[i][1] = start;
      this.array[i][2] = start;
      this.array[i][3] = end;
      this.array[i][4] = end;

      i++;
    }
  }

}
