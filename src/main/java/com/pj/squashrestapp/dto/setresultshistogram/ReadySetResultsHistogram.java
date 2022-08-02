package com.pj.squashrestapp.dto.setresultshistogram;

import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.PlayerDto;
import lombok.Getter;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ReadySetResultsHistogram {

    private LeagueDto league;
    private Set<ReadySetResultsPlayer> setResultsPlayers;
    private Set<SetResultForHistogram> uniqueResults;

    public ReadySetResultsHistogram(final SetResultsLeagueHistogramDto dto) {
        this.league = dto.getLeague();
        this.setResultsPlayers = new TreeSet<>();
        this.uniqueResults = new TreeSet<>();
        for (final PlayerDto player :
                dto.getPlayerDtoSetResultsPlayerHistogramDtoMap().keySet()) {
            final SetResultsPlayerHistogramDto setResultsPlayerHistogramDto =
                    dto.getPlayerDtoSetResultsPlayerHistogramDtoMap().get(player);
            final TreeMap<SetResultForHistogram, Integer> resultToCountMap =
                    setResultsPlayerHistogramDto.getResultToCountMap();
            this.setResultsPlayers.add(new ReadySetResultsPlayer(player, resultToCountMap));
            this.uniqueResults.addAll(resultToCountMap.keySet());
        }
    }
}
