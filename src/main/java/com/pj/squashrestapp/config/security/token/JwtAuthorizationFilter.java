package com.pj.squashrestapp.config.security.token;

import static com.pj.squashrestapp.config.security.token.TokenConstants.HEADER_STRING;
import static com.pj.squashrestapp.config.security.token.TokenConstants.TOKEN_PREFIX;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 *
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private final SecretKeyHolder secretKeyHolder;
  private final PlayerRepository playerRepository;

  public JwtAuthorizationFilter(final AuthenticationManager authManager,
                                final SecretKeyHolder secretKeyHolder,
                                final PlayerRepository playerRepository) {
    super(authManager);
    this.secretKeyHolder = secretKeyHolder;
    this.playerRepository = playerRepository;
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

      final String playerUuidAsString = claims.get("uid", String.class);
      final UUID playerUuid = UUID.fromString(playerUuidAsString);

      final Player player = playerRepository
              .fetchForAuthorizationByUuid(playerUuid)
              .orElseThrow(() -> new RuntimeException("User with given UUID does not exist!"));

      log.debug("\nToken Info:\n\t UUID:\t\t {}\n\t user:\t\t {}\n\t issued:\t {}\n\t expires:\t {}",
              player.getUuid(),
              player.getUsername(),
              claims.getIssuedAt(),
              claims.getExpiration());

      final UserDetailsImpl userDetailsImpl = new UserDetailsImpl(player);

      // checking if the account is activated
      if (!userDetailsImpl.isEnabled()) {
        throw new AccountNotActivatedException("Account has not been activated, maybe you should check your emails!");
      }

      // checking if password session UUID matches
      final UUID tokenPasswordSessionUuid = UUID.fromString(claims.get("pid", String.class));
      final UUID userPasswordSessionUuid = userDetailsImpl.getPasswordSessionUuid();
      if (!tokenPasswordSessionUuid.equals(userPasswordSessionUuid)) {
        throw new RuntimeException("Password Session Token is invalid (which means that the password has been changed recently).");
      }

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