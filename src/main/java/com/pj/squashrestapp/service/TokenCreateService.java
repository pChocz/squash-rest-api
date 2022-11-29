package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.security.token.SecretKeyHolder;
import com.pj.squashrestapp.dto.TokenPair;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RefreshToken;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RefreshTokenRepository;
import com.pj.squashrestapp.util.ErrorCode;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static com.pj.squashrestapp.config.security.token.TokenConstants.ACCESS_TOKEN_EXPIRATION_TIME_DAYS;
import static com.pj.squashrestapp.config.security.token.TokenConstants.REFRESH_TOKEN_EXPIRATION_TIME_DAYS;
import static com.pj.squashrestapp.config.security.token.TokenConstants.TOKEN_PREFIX;
import static net.logstash.logback.argument.StructuredArguments.v;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenCreateService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PlayerRepository playerRepository;
    private final SecretKeyHolder secretKeyHolder;

    public TokenPair attemptToCreateNewTokensPairUsingRefreshToken(final UUID oldRefreshTokenUuid) {
        final RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(oldRefreshTokenUuid);

        if (oldRefreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_REFRESH_TOKEN);

        } else if (isTokenExpired(oldRefreshToken)) {
            refreshTokenRepository.delete(oldRefreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // if token is fine, we can delete it from db and create new one
        final Player player = oldRefreshToken.getPlayer();
        player.incrementSuccessfulLoginAttempts();
        playerRepository.save(player);
        refreshTokenRepository.delete(oldRefreshToken);
        return createTokensPairForPlayer(player, false);
    }

    private boolean isTokenExpired(final RefreshToken refreshToken) {
        return refreshToken.getExpirationDateTime().isBefore(LocalDateTime.now());
    }

    public TokenPair createTokensPairForPlayer(final Player player, final boolean adminLogin) {
        // TEST CODE:
//            final LocalDateTime accessTokenExpirationDate = LocalDateTime.now().plusSeconds(20);
//            final LocalDateTime refreshTokenExpirationDate = LocalDateTime.now().plusSeconds(40);

        // PRODUCTION CODE:
        final LocalDateTime accessTokenExpirationDate = LocalDateTime.now().plusDays(ACCESS_TOKEN_EXPIRATION_TIME_DAYS);
        final LocalDateTime refreshTokenExpirationDate = LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_TIME_DAYS);

        final String jwtToken = createJwtTokenForPlayer(player, accessTokenExpirationDate, adminLogin);

        if (adminLogin) {
            // do not create proper refresh token
            return new TokenPair(TOKEN_PREFIX + jwtToken, UUID.randomUUID());

        } else {
            final RefreshToken newRefreshToken = new RefreshToken(UUID.randomUUID(), player, refreshTokenExpirationDate);
            refreshTokenRepository.save(newRefreshToken);
            return new TokenPair(TOKEN_PREFIX + jwtToken, newRefreshToken.getToken());
        }
    }

    private String createJwtTokenForPlayer(final Player player, final LocalDateTime expirationDate, final boolean adminLogin) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .claim("uid", player.getUuid())
                .claim("pid", player.getPasswordSessionUuid())
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant()));

        if (adminLogin) {
            jwtBuilder = jwtBuilder.claim("adl", "1");
        }

        final String compactJwt = jwtBuilder
                .signWith(secretKeyHolder.getSecretKey())
                .compact();

        log.info(
                "JWT token created for player {}",
                v("player", player.getUsername()),
                v("token", compactJwt)
        );

        return compactJwt;
    }
}
