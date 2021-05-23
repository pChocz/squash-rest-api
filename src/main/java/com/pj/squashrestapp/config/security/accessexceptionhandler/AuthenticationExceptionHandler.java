package com.pj.squashrestapp.config.security.accessexceptionhandler;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;

/**
 *
 */
public class AuthenticationExceptionHandler {

  public AuthenticationExceptionHandler(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AuthenticationException e) throws IOException {

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    try (final PrintWriter writer = response.getWriter()) {
      writer.write(new JSONObject()
              .appendField("status", HttpServletResponse.SC_UNAUTHORIZED)
              .appendField("user", "ANONYMOUS USER")
              .appendField("timestamp", LocalDateTime.now(UTC_ZONE_ID).toString())
              .appendField("message", "NOT AUTHENTICATED")
              .toString());
    }

  }

}
