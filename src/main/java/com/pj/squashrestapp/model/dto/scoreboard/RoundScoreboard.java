package com.pj.squashrestapp.model.dto.scoreboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Getter
public class RoundScoreboard {

  private final String leagueName;

  private final UUID seasonUuid;
  private final int seasonNumber;

  private final UUID roundUuid;
  private final int roundNumber;
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
  private final LocalDate roundDate;

  private boolean finishedState;

  private UUID previousRoundUuid;
  private UUID nextRoundUuid;

  @JsonIgnore
  private final List<Integer> playersPerGroup;
  private final List<RoundGroupScoreboard> roundGroupScoreboards;

  public RoundScoreboard(final Round round, final UUID previousRoundUuid, final UUID nextRoundUuid) {
    this.previousRoundUuid = previousRoundUuid;
    this.nextRoundUuid = nextRoundUuid;

    this.leagueName = round.getSeason().getLeague().getName();

    this.seasonUuid = round.getSeason().getUuid();
    this.seasonNumber = round.getSeason().getNumber();

    this.roundUuid = round.getUuid();
    this.roundNumber = round.getNumber();
    this.roundDate = round.getDate();
    this.finishedState = round.isFinished();

    this.roundGroupScoreboards = new ArrayList<>();
    this.playersPerGroup = new ArrayList<>();
  }

  public RoundScoreboard(final Round round) {
    this.leagueName = round.getSeason().getLeague().getName();

    this.seasonUuid = round.getSeason().getUuid();
    this.seasonNumber = round.getSeason().getNumber();

    this.roundUuid = round.getUuid();
    this.roundNumber = round.getNumber();
    this.roundDate = round.getDate();

    this.roundGroupScoreboards = new ArrayList<>();
    this.playersPerGroup = new ArrayList<>();
  }

  public void addRoundGroupNew(final RoundGroup roundGroup) {
    final List<MatchDetailedDto> matches = MatchExtractorUtil.extractAllMatches(roundGroup);
    final RoundGroupScoreboard scoreboard = new RoundGroupScoreboard(matches);
    roundGroupScoreboards.add(scoreboard);
    playersPerGroup.add(scoreboard.getScoreboardRows().size());
  }

  public void assignPointsAndPlaces(final List<Integer> xpPoints) {
    int i = 0;
    for (final RoundGroupScoreboard scoreboard : roundGroupScoreboards) {
      int j = 1;
      for (final ScoreboardRow scoreboardRow : scoreboard.getScoreboardRows()) {
        scoreboardRow.setPlaceInRound(i + 1);
        scoreboardRow.setPlaceInGroup(j++);
        scoreboardRow.setXpEarned(xpPoints.get(i++));
      }
    }
  }

}
