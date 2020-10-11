package com.pj.squashrestapp.config.security.accessexceptionhandler;

import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

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
