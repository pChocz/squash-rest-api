package com.pj.squashrestapp.config;

import net.minidev.json.JSONObject;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 *
 */
public class AuthenticationExceptionHandler {

  public AuthenticationExceptionHandler(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final AuthenticationException e) throws IOException {

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    try (final PrintWriter writer = response.getWriter()) {
      writer.write(new JSONObject()
              .appendField("response", HttpServletResponse.SC_FORBIDDEN)
              .appendField("user", "ANONYMOUS USER")
              .appendField("timestamp", new Date(System.currentTimeMillis()))
              .appendField("message", "NOT AUTHENTICATED")
              .toString());
    }

  }

}
