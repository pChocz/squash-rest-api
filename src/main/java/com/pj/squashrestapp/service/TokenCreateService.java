package com.pj.squashrestapp.service;

import static com.pj.squashrestapp.config.security.token.TokenConstants.ACCESS_TOKEN_EXPIRATION_TIME_DAYS;
import static com.pj.squashrestapp.config.security.token.TokenConstants.REFRESH_TOKEN_EXPIRATION_TIME_DAYS;

import com.pj.squashrestapp.config.security.token.SecretKeyHolder;
import com.pj.squashrestapp.dto.TokenPair;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RefreshToken;
import com.pj.squashrestapp.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCreateService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final SecretKeyHolder secretKeyHolder;

  public TokenPair attemptToCreateNewTokensPair(final UUID oldRefreshTokenUuid) {
    final RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(oldRefreshTokenUuid);

    if (oldRefreshToken == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token invalid!");

    } else if (isTokenExpired(oldRefreshToken)) {
      refreshTokenRepository.delete(oldRefreshToken);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired!");
    }

    // if token is fine, we can delete it from db and create new one
    final Player player = oldRefreshToken.getPlayer();
    refreshTokenRepository.delete(oldRefreshToken);
    return createTokensPairForPlayer(player);
  }

  private boolean isTokenExpired(final RefreshToken refreshToken) {
    return refreshToken.getExpirationDateTime().isBefore(LocalDateTime.now());
  }

  public TokenPair createTokensPairForPlayer(final Player player) {
    // TEST CODE:
    //    final LocalDateTime accessTokenExpirationDate = LocalDateTime.now().plusSeconds(20);
    //    final LocalDateTime refreshTokenExpirationDate = LocalDateTime.now().plusSeconds(40);

    // PRODUCTION CODE:
    final LocalDateTime accessTokenExpirationDate =
        LocalDateTime.now().plusDays(ACCESS_TOKEN_EXPIRATION_TIME_DAYS);
    final LocalDateTime refreshTokenExpirationDate =
        LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_TIME_DAYS);

    final String jwtToken = createJwtTokenForPlayer(player, accessTokenExpirationDate);

    final RefreshToken newRefreshToken =
        new RefreshToken(UUID.randomUUID(), player, refreshTokenExpirationDate);
    refreshTokenRepository.save(newRefreshToken);

    return new TokenPair(jwtToken, newRefreshToken.getToken());
  }

  private String createJwtTokenForPlayer(final Player player, final LocalDateTime expirationDate) {
    return Jwts.builder()
        .claim("uid", player.getUuid())
        .claim("pid", player.getPasswordSessionUuid())
        .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
        .setExpiration(Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant()))
        .signWith(secretKeyHolder.getSecretKey())
        .compact();
  }
}
