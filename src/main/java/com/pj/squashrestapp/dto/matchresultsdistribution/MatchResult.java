package com.pj.squashrestapp.dto.matchresultsdistribution;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class MatchResult implements Comparable<MatchResult> {

    @EqualsAndHashCode.Include
    private int won;
    @EqualsAndHashCode.Include
    private int lost;
    private String result;
    private int diff;

    public MatchResult(final int won, final int lost) {
        this.won = won;
        this.lost = lost;
    }

    @Override
    public int compareTo(final MatchResult that) {
        return Comparator.comparingInt(MatchResult::getWon)
                .thenComparingInt(MatchResult::getDiff)
                .compare(this, that);
    }

    public String getResult() {
        return won + ":" + lost;
    }

    public int getDiff() {
        return won - lost;
    }

}
