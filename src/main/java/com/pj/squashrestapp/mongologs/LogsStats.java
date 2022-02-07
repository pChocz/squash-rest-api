package com.pj.squashrestapp.mongologs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @JsonFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT)
  private LocalDateTime minDateTime;
  @JsonFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT)
  private LocalDateTime maxDateTime;

  private Long minDuration;
  private Long maxDuration;

  private Long minQueryCount;
  private Long maxQueryCount;

}
