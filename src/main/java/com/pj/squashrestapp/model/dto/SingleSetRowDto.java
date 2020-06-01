package com.pj.squashrestapp.model.dto;

import lombok.Value;

import java.util.Date;

/**
 *
 */
@SuppressWarnings("unused")
@Value
public class SingleSetRowDto {

  Long matchId;

  Long firstPlayerId;
  String firstPlayerName;

  Long secondPlayerId;
  String secondPlayerName;

  Long roundGroupId;
  int roundGroupNumber;

  Long roundId;
  int roundNumber;
  Date roundDate;

  Long seasonId;
  int seasonNumber;

  int setNumber;
  int firstPlayerScore;
  int secondPlayerScore;

  public boolean hasBeenPlayed() {
//    return !(firstPlayerScore == 0 && secondPlayerScore == 0);
    return true;
  }

}
