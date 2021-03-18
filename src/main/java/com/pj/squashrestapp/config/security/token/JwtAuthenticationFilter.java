package com.pj.squashrestapp.config.security.token;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.service.TokenCreateService;
import com.pj.squashrestapp.service.TokenPair;
import com.pj.squashrestapp.util.TimeLogUtil;
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

import static com.pj.squashrestapp.config.security.token.TokenConstants.EXPOSE_HEADER_STRING;
import static com.pj.squashrestapp.config.security.token.TokenConstants.HEADER_REFRESH_STRING;
import static com.pj.squashrestapp.config.security.token.TokenConstants.HEADER_STRING;
import static com.pj.squashrestapp.config.security.token.TokenConstants.TOKEN_PREFIX;

/**
 *
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final TokenCreateService tokenCreateService;
  private final PlayerRepository playerRepository;

  public JwtAuthenticationFilter(final AuthenticationManager authenticationManager,
                                 final TokenCreateService tokenCreateService,
                                 final PlayerRepository playerRepository) {
    this.authenticationManager = authenticationManager;
    this.tokenCreateService = tokenCreateService;
    this.playerRepository = playerRepository;
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest req,
                                              final HttpServletResponse res) throws AuthenticationException {

    final String usernameOrEmail = req.getParameter("usernameOrEmail").trim();

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
    final Player player = playerRepository.findByUuid(principal.getUuid());
    final TokenPair tokensPair = tokenCreateService.createTokensPairForPlayer(player);

    res.addHeader(HEADER_STRING, TOKEN_PREFIX + tokensPair.getJwtAccessToken());
    res.addHeader(HEADER_REFRESH_STRING, tokensPair.getRefreshToken().toString());
    res.addHeader(EXPOSE_HEADER_STRING, HEADER_STRING + ", " + HEADER_REFRESH_STRING);
  }

}
