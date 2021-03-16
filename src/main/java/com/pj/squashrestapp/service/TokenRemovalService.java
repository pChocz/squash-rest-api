package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RefreshToken;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.repository.PasswordResetTokenRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RefreshTokenRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRemovalService {

  private final VerificationTokenRepository verificationTokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final PlayerRepository playerRepository;


  public PlayerDetailedDto extractPlayerByPasswordResetToken(final UUID token) {
    final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
    final UUID playerUuid = passwordResetToken.getPlayer().getUuid();
    final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).get();
    final PlayerDetailedDto playerDetailedDto = new PlayerDetailedDto(player);
    return playerDetailedDto;
  }

  /**
   * Finds all expired temporary tokens in the database and removes them permanently.
   */
  public void removeExpiredTokensFromDb() {
    final LocalDateTime now = LocalDateTime.now(UTC_ZONE_ID);

    final List<VerificationToken> expiredVerificationTokens = verificationTokenRepository.findAllByExpirationDateTimeBefore(now);
    final List<RefreshToken> expiredRefreshTokens = refreshTokenRepository.findAllByExpirationDateTimeBefore(now);
    final List<PasswordResetToken> expiredPasswordResetTokens = passwordResetTokenRepository.findAllByExpirationDateTimeBefore(now);
    final int tokensCount = expiredVerificationTokens.size() + expiredRefreshTokens.size() + expiredPasswordResetTokens.size();

    if (tokensCount > 0) {
      verificationTokenRepository.deleteAll(expiredVerificationTokens);
      refreshTokenRepository.deleteAll(expiredRefreshTokens);
      passwordResetTokenRepository.deleteAll(expiredPasswordResetTokens);
      log.info("Succesfully removed {} expired tokens.", tokensCount);

    } else {
      log.info("No expired tokens to remove this time");
    }
  }

}
