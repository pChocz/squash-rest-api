package com.pj.squashrestapp.config.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.pj.squashrestapp.config.PlayerAuthDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.pj.squashrestapp.config.security.SecurityConstants.HEADER_STRING;
import static com.pj.squashrestapp.config.security.SecurityConstants.SECRET_KEY;
import static com.pj.squashrestapp.config.security.SecurityConstants.TOKEN_PREFIX;

/**
 *
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private final UserDetailsService userDetailsService;
  private final SecretKeyHolder secretKeyHolder;

  public JwtAuthorizationFilter(final AuthenticationManager authManager,
                                final UserDetailsService userDetailsService,
                                final SecretKeyHolder secretKeyHolder) {
    super(authManager);
    this.userDetailsService = userDetailsService;
    this.secretKeyHolder = secretKeyHolder;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest req,
                                  final HttpServletResponse res,
                                  final FilterChain chain) throws IOException, ServletException {
    final UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
    if (authentication != null) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    chain.doFilter(req, res);
  }

  /**
   * <pre>
   * User authentication is performed in this method, based on the
   * token received in the HTTP request.
   *
   * NULL is returned in case of unsuccessful authentication,
   * which can happen because of following reasons (i.a.):
   * - MalformedJwtException      -> Token does not seem to be valid at all
   * - SignatureException         -> Token has proper syntax but signature does not match the content
   * - UsernameNotFoundException  -> Token is valid but username does not exist
   * - ExpiredJwtException        -> Token has expired, obviously
   * - MalformedJsonException     -> Token has wrong JSON syntax
   * </pre>
   */
  @SuppressWarnings("ProhibitedExceptionCaught")
  private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
    final String tokenWithHeader = request.getHeader(HEADER_STRING);
    if (tokenWithHeader == null) {
      log.warn("Authorization is missing in the request");
      return null;
    }

    final String token = tokenWithHeader.replace(TOKEN_PREFIX, "");
    try {
      final Claims claims = Jwts
              .parserBuilder()
              .setSigningKey(secretKeyHolder.getSecretKey())
              .build()
              .parseClaimsJws(token)
              .getBody();

      log.info("\nToken Info:\n\t user:\t\t {}\n\t issued:\t {}\n\t expires:\t {}",
              claims.getSubject(),
              claims.getIssuedAt(),
              claims.getExpiration());

      final PlayerAuthDetails player = (PlayerAuthDetails) userDetailsService.loadUserByUsername(claims.getSubject());
      return new UsernamePasswordAuthenticationToken(
              player.getUsername(),
              player.getPassword(),
              player.getAuthorities());

    } catch (final Exception e) {
      log.warn(e.getMessage());
      return null;
    }
  }

}