package com.pj.squashrestapp.mongologs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class LogAggregateByMethod {

  @Id
  private String methodName;
  private Long durationSum;
  private Long durationAvg;
  private Long queryCountSum;
  private Long countSum;
}
