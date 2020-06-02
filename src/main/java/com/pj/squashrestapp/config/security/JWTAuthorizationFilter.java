package com.pj.squashrestapp.config.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pj.squashrestapp.config.security.SecurityConstants.HEADER_STRING;
import static com.pj.squashrestapp.config.security.SecurityConstants.SECRET_KEY;
import static com.pj.squashrestapp.config.security.SecurityConstants.TOKEN_PREFIX;

/**
 *
 */
@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  public JWTAuthorizationFilter(final AuthenticationManager authManager) {
    super(authManager);
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

        final String user = claims.getSubject();

        final ArrayList<String> authorities = (ArrayList<String>) claims.get("roles");

        final List<GrantedAuthority> grantedAuthorities = authorities
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        if (user != null) {
          return new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
        }

      } catch (final TokenExpiredException e) {
        log.info(e.getMessage());
      }

      return null;
    }
    return null;
  }
}