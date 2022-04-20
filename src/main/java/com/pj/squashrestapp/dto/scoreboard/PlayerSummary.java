package com.pj.squashrestapp.dto.scoreboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSummary {

    private ScoreboardRow scoreboardRow;
    private int leagues;
    private int seasons;
    private int rounds;

    @Override
    public String toString() {
        return scoreboardRow.getPlayer() + " | leagues: " + leagues + " | seasons: " + seasons + " | rounds: " + rounds;
    }
}
