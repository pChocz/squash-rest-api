package com.pj.squashrestapp.config.security;

import com.pj.squashrestapp.config.PlayerAuthDetails;
import io.jsonwebtoken.Jwts;
import org.apache.commons.io.IOUtils;
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
import java.util.Base64;
import java.util.Date;

import static com.pj.squashrestapp.config.security.SecurityConstants.EXPIRATION_TIME;
import static com.pj.squashrestapp.config.security.SecurityConstants.HEADER_STRING;
import static com.pj.squashrestapp.config.security.SecurityConstants.SECRET_KEY;
import static com.pj.squashrestapp.config.security.SecurityConstants.TOKEN_PREFIX;

/**
 *
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  public JWTAuthenticationFilter(final AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest req, final HttpServletResponse res)
          throws AuthenticationException {
    try {
      final String reqInput = IOUtils.toString(req.getInputStream());
      final byte[] decode = Base64.getDecoder().decode(reqInput);
      final String[] credentials = new String(decode).split(":");
      final var authentication = new UsernamePasswordAuthenticationToken(credentials[0], credentials[1], new ArrayList<>());
      final Authentication authenticate = authenticationManager.authenticate(authentication);

      return authenticate;

    } catch (final IOException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest req,
                                          final HttpServletResponse res,
                                          final FilterChain chain,
                                          final Authentication auth) throws IOException, ServletException {
    final PlayerAuthDetails principal = (PlayerAuthDetails) auth.getPrincipal();

    final String token = Jwts
            .builder()
            .setSubject(principal.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SECRET_KEY)
            .compact();

    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
  }

}
