package com.pj.squashrestapp.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScoreEventType {
    FIRST_PLAYER_CALLS_LET(true),
    SECOND_PLAYER_CALLS_LET(true),
    FIRST_PLAYER_SCORES(true),
    SECOND_PLAYER_SCORES(true),

    GAME_BEGINS(false),
    GAME_ENDS(false),
    MATCH_BEGINS(false),
    MATCH_ENDS(false);

    final boolean rally;
}
