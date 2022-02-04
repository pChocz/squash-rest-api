package com.pj.squashrestapp.model;

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

@Document("logEntries")
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class LogEntry {

  @Id
  private String id;

  @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
  private LocalDateTime timestamp;

  private String username;
  private String className;
  private String methodName;
  private String arguments;
  private Long duration;
  private long queryCount;
  private String errorMessage;
  private String stackTrace;
  private String message;
  private String type;

}
