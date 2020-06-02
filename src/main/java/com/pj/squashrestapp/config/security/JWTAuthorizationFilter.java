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

import static com.pj.squashrestapp.config.security.SecurityConstants.HEADER_STRING;
import static com.pj.squashrestapp.config.security.SecurityConstants.SECRET_KEY;
import static com.pj.squashrestapp.config.security.SecurityConstants.TOKEN_PREFIX;

/**
 *
 */
@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private final UserDetailsService userDetailsService;

  public JWTAuthorizationFilter(final AuthenticationManager authManager,
                                final UserDetailsService userDetailsService) {
    super(authManager);
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest req,
                                  final HttpServletResponse res,
                                  final FilterChain chain) throws IOException, ServletException {
    final String header = req.getHeader(HEADER_STRING);

    if (header != null && header.startsWith(TOKEN_PREFIX)) {
      final UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
    final String tokenWithHeader = request.getHeader(HEADER_STRING);

    if (tokenWithHeader != null) {
      final String token = tokenWithHeader.replace(TOKEN_PREFIX, "");

      try {
        final Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        final String username = claims.getSubject();
        final PlayerAuthDetails player = (PlayerAuthDetails) userDetailsService.loadUserByUsername(username);
        final List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) player.getAuthorities();

        return new UsernamePasswordAuthenticationToken(
                player.getUsername(),
                null,
                grantedAuthorities);

      } catch (final TokenExpiredException | ExpiredJwtException e) {
        log.info(e.getMessage());
      }

      return null;
    }
    return null;
  }
}