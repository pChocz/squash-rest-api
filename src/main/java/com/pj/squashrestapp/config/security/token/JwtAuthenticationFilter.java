package com.pj.squashrestapp.config.security.token;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.util.TimeLogUtil;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.pj.squashrestapp.config.security.token.TokenConstants.EXPIRATION_TIME;
import static com.pj.squashrestapp.config.security.token.TokenConstants.EXPOSE_HEADER_STRING;
import static com.pj.squashrestapp.config.security.token.TokenConstants.HEADER_STRING;
import static com.pj.squashrestapp.config.security.token.TokenConstants.TOKEN_PREFIX;

/**
 *
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final SecretKeyHolder secretKeyHolder;

  public JwtAuthenticationFilter(final AuthenticationManager authenticationManager,
                                 final SecretKeyHolder secretKeyHolder) {
    this.authenticationManager = authenticationManager;
    this.secretKeyHolder = secretKeyHolder;
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest req,
                                              final HttpServletResponse res) throws AuthenticationException {

    final String usernameOrEmail = req.getParameter("usernameOrEmail");

    try {
      final int numberOfParams = req.getParameterMap().size();
      final String password = req.getParameter("password");

      if (numberOfParams != 2 || usernameOrEmail == null || password == null) {
        throw new WrongCredentialsFormatException("Wrong format of credentials received");
      }

      final long startTime = System.nanoTime();
      final var authentication = new UsernamePasswordAuthenticationToken(usernameOrEmail, password, new ArrayList<>());
      final var auth = authenticationManager.authenticate(authentication);
      log.info("Authentication took {} s", TimeLogUtil.getDurationSecondsRounded(startTime));
      log.info("User [{}] has logged in", getPrincipal(auth).getUsername());
      return auth;

    } catch (final AuthenticationException e) {
      throw new WrongCredentialsFormatException(e.getMessage() + " | User: [" + usernameOrEmail + "]");
    }
  }

  private UserDetailsImpl getPrincipal(final Authentication auth) {
    return (UserDetailsImpl) auth.getPrincipal();
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest req,
                                          final HttpServletResponse res,
                                          final FilterChain chain,
                                          final Authentication auth) throws IOException, ServletException {
    final UserDetailsImpl principal = getPrincipal(auth);

    final String token = Jwts
            .builder()
            .claim("uid", principal.getUuid())
            .claim("pid", principal.getPasswordSessionUuid())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(secretKeyHolder.getSecretKey())
            .compact();

    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    res.addHeader(EXPOSE_HEADER_STRING, HEADER_STRING);
  }

}
