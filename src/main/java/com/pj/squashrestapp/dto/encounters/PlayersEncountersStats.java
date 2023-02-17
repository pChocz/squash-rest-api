package com.pj.squashrestapp.dto.encounters;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlayersEncountersStats {

    private PlayerDto firstPlayer;
    private PlayerDto secondPlayer;
    private List<PlayersEncounter> playersEncounters = new ArrayList<>();

    public PlayersEncountersStats(final List<RoundScoreboard> roundScoreboards, final List<Player> players) {
        this.firstPlayer = new PlayerDto(players.get(0));
        this.secondPlayer = new PlayerDto(players.get(1));
        for (final RoundScoreboard roundScoreboard : roundScoreboards) {
            final PlayersEncounter playersEncounter = new PlayersEncounter(roundScoreboard, firstPlayer, secondPlayer);
            if (shouldBeIncluded(playersEncounter)) {
                this.playersEncounters.add(playersEncounter);
            }
        }
    }

    /**
     * rounds for players encounters stats are filtered to include only finished ones
     * (where all matches should already be completed), so this method should usually
     * return TRUE. There are however rare cases where it may be useful to perform
     * additional checks, and it can prevent throwing an exception.
     */
    private boolean shouldBeIncluded(final PlayersEncounter playersEncounter) {
        if (playersEncounter.getWinner() == null) {
            return false;
        }
        final MatchSimpleDto directMatch = playersEncounter.getDirectMatch();
        if (directMatch == null || directMatch.getWinner() != null) {
            // if there is no direct match (players have played in different group)
            // or if there is a direct match where winner is concluded.
            return true;
        }
        // should never reach this point
        return false;
    }
}
