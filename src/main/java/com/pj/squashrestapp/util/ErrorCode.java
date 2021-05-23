package com.pj.squashrestapp.util;

import com.pj.squashrestapp.config.UserDetailsImpl;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

/** */
@UtilityClass
public class ErrorCode {

  public static final String LEAGUE_NOT_FOUND = "LEAGUE_NOT_FOUND";
  public static final String SEASON_NOT_FOUND = "LEAGUE_NOT_FOUND";
  public static final String ROUND_NOT_FOUND = "LEAGUE_NOT_FOUND";
}
