package com.pj.squashrestapp.dto.matchresultsdistribution;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class MatchResultCount implements Comparable<MatchResultCount> {

    private MatchResult matchResult;
    private int matchesWon;

    public MatchResultCount(final MatchResult matchResult, final int matchesWon) {
        this.matchResult = matchResult;
        this.matchesWon = matchesWon;
    }

    @Override
    public int compareTo(final MatchResultCount that) {
        return Comparator.comparing(MatchResultCount::getMatchResult)
                .compare(this, that);
    }

}
