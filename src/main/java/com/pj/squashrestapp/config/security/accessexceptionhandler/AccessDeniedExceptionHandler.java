package com.pj.squashrestapp.config.security.accessexceptionhandler;

import com.pj.squashrestapp.util.GeneralUtil;
import net.minidev.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/**
 *
 */
public class AccessDeniedExceptionHandler {

  public AccessDeniedExceptionHandler(final HttpServletRequest request,
                                      final HttpServletResponse response,
                                      final AccessDeniedException e) throws IOException {

    final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
    final String username = token.getName();
    final String[] authorities = extractAuthoritiesFromToken(token);

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    try (final PrintWriter writer = response.getWriter()) {
      writer.write(new JSONObject()
              .appendField("response", HttpServletResponse.SC_FORBIDDEN)
              .appendField("user", username)
              .appendField("authorities", authorities)
              .appendField("timestamp", LocalDateTime.now(UTC_ZONE_ID).toString())
              .appendField("message", "FORBIDDEN")
              .toString());
    }

  }

  private String[] extractAuthoritiesFromToken(final UsernamePasswordAuthenticationToken token) {
    return token
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toArray(String[]::new);
  }

}
