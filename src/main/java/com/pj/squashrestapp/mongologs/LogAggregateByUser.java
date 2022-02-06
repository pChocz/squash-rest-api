package com.pj.squashrestapp.mongologs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class LogAggregateByUser {

  @Id
  private String username;
  private Long durationSum;
  private Long queryCountSum;
  private Long countSum;
}
