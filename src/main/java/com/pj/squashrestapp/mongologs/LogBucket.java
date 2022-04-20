package com.pj.squashrestapp.mongologs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class LogBucket implements Comparable<LogBucket> {

    private Date id;
    private Integer countSum;

    @Override
    public int compareTo(LogBucket o) {
        return this.getId().compareTo(o.getId());
    }
}
