package com.pj.squashrestapp.config.security.token;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.model.BlacklistedToken;
import com.pj.squashrestapp.repository.BlacklistedTokensRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.pj.squashrestapp.config.security.token.TokenConstants.HEADER_STRING;
import static com.pj.squashrestapp.config.security.token.TokenConstants.TOKEN_PREFIX;

/**
 *
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private final UserDetailsService userDetailsService;
  private final SecretKeyHolder secretKeyHolder;
  private final BlacklistedTokensRepository blacklistedTokensRepository;

  public JwtAuthorizationFilter(final AuthenticationManager authManager,
                                final UserDetailsService userDetailsService,
                                final SecretKeyHolder secretKeyHolder,
                                final BlacklistedTokensRepository blacklistedTokensRepository) {
    super(authManager);
    this.userDetailsService = userDetailsService;
    this.secretKeyHolder = secretKeyHolder;
    this.blacklistedTokensRepository = blacklistedTokensRepository;
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
   * - TokenBlacklistedException  -> Token has been blacklisted
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
      // checking if token is on the blacklist
      final BlacklistedToken tokenFromBlacklist = blacklistedTokensRepository.findByToken(token);
      if (tokenFromBlacklist != null) {
        throw new TokenBlacklistedException("Token has been blacklisted, it cannot be authenticated");
      }

      final Claims claims = Jwts
              .parserBuilder()
              .setSigningKey(secretKeyHolder.getSecretKey())
              .build()
              .parseClaimsJws(token)
              .getBody();
      final String username = claims.getSubject();

      log.info("\nToken Info:\n\t user:\t\t {}\n\t issued:\t {}\n\t expires:\t {}",
              username,
              claims.getIssuedAt(),
              claims.getExpiration());

      final UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
      final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
              userDetailsImpl,
              userDetailsImpl.getPassword(),
              userDetailsImpl.getAuthorities());
      return usernamePasswordAuthenticationToken;

    } catch (final Exception e) {
      log.warn(e.getMessage());
      return null;
    }
  }

}