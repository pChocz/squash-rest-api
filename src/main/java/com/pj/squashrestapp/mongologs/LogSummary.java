package com.pj.squashrestapp.mongologs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LogSummary {

    private LogsStats allLogsStats;
    private LogsStats filteredLogsStats;
    private List<LogAggregateByMethod> filteredLogsAggregateByMethod;
    private List<LogAggregateByUser> filteredLogsAggregateByUser;
    private List<LogBucket> logBuckets;

    private long timeTookMillis;
}
