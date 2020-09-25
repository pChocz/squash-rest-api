package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.BlacklistedToken;
import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.repository.BlacklistedTokenRepository;
import com.pj.squashrestapp.repository.PasswordResetTokenRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

  private final BlacklistedTokenRepository blacklistedTokenRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final PlayerRepository playerRepository;


  /**
   *
   * @param token
   * @return
   */
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

    final List<BlacklistedToken> expiredBlacklistedTokens = blacklistedTokenRepository.findAllByExpirationDateTimeBefore(now);
    final List<VerificationToken> expiredVerificationTokens = verificationTokenRepository.findAllByExpirationDateTimeBefore(now);
    final List<PasswordResetToken> expiredPasswordResetTokens = passwordResetTokenRepository.findAllByExpirationDateTimeBefore(now);
    final int tokensCount = expiredBlacklistedTokens.size() + expiredBlacklistedTokens.size() + expiredPasswordResetTokens.size();

    if (tokensCount > 0) {
      blacklistedTokenRepository.deleteAll(expiredBlacklistedTokens);
      verificationTokenRepository.deleteAll(expiredVerificationTokens);
      passwordResetTokenRepository.deleteAll(expiredPasswordResetTokens);
      log.info("Succesfully removed {} expired tokens.", tokensCount);

    } else {
      log.info("No expired tokens to remove this time");
    }
  }

}

















