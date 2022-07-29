package com.pj.squashrestapp.dto.setresultshistogram;

import com.pj.squashrestapp.dto.PlayerDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;

@Getter
public class ReadySetResultsPlayer implements Comparable<ReadySetResultsPlayer> {

    private final PlayerDto player;
    private final List<ReadySetResultCount> setResultCounts;

    public ReadySetResultsPlayer(
            final PlayerDto player, final SortedMap<SetResultForHistogram, Integer> resultToCountMap) {
        this.player = player;
        this.setResultCounts = new ArrayList<>();
        resultToCountMap.keySet().forEach(setResultForHistogram -> {
            final int count = resultToCountMap.get(setResultForHistogram);
            this.setResultCounts.add(new ReadySetResultCount(
                    setResultForHistogram.getFirst(), setResultForHistogram.getSecond(), count));
        });
    }

    @Override
    public int compareTo(final ReadySetResultsPlayer that) {
        return Comparator.comparing(PlayerDto::getUsername).compare(this.getPlayer(), that.getPlayer());
    }
}
