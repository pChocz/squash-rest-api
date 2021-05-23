package com.pj.squashrestapp.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/** */
@Builder
@Getter
public class ErrorResponse {

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z", timezone = "UTC")
  private final LocalDateTime timestamp;

  private final int status;
  private final String message;
  private final String path;
}
