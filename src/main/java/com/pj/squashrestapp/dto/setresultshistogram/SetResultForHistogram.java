package com.pj.squashrestapp.dto.setresultshistogram;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class SetResultForHistogram implements Comparable<SetResultForHistogram> {

    private int first;
    private int second;
    private String result;
    private int greatest;
    private int diff;

    public SetResultForHistogram(final int first, final int second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(final SetResultForHistogram that) {
        return Comparator.comparingInt(SetResultForHistogram::getGreatest)
                .thenComparingInt(SetResultForHistogram::getDiff)
                .compare(this, that);
    }

    public String getResult() {
        return first + ":" + second;
    }

    public int getGreatest() {
        return Math.max(this.first, this.second);
    }

    public int getDiff() {
        return this.first - this.second;
    }
}
