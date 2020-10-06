package com.pj.squashrestapp.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 *
 */
@Builder
@Getter
public class ErrorResponse {

  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
  private final LocalDateTime timestamp;
  private final int status;
  private final String message;
  private final String path;

}
