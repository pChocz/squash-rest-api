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
public class MatchResultCount implements Comparable<MatchResultCount> {

    @EqualsAndHashCode.Include
    private MatchResult matchResult;
    private int matchesWon;
    private int matchesLost;

    public MatchResultCount(final MatchResult matchResult) {
        this.matchResult = matchResult;
    }

    @Override
    public int compareTo(final MatchResultCount that) {
        return Comparator.comparing(MatchResultCount::getMatchResult)
                .compare(this, that);
    }

}
