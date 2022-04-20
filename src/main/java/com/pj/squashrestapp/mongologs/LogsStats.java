package com.pj.squashrestapp.mongologs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class LogsStats {

    private long timeTook;

    private Long count;

    private List<String> usernames;
    private List<String> logTypes;
    private List<String> classNames;

    private Date minDateTime;
    private Date maxDateTime;

    private Long minDuration;
    private Long maxDuration;

    private Long minQueryCount;
    private Long maxQueryCount;
}
