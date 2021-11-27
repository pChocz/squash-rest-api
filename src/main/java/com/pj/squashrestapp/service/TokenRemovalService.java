package com.pj.squashrestapp.service;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.MagicLoginLinkToken;
import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RefreshToken;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.repository.MagicLinkLoginTokenRepository;
import com.pj.squashrestapp.repository.PasswordResetTokenRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RefreshTokenRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRemovalService {

  private final VerificationTokenRepository verificationTokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final MagicLinkLoginTokenRepository magicLinkLoginTokenRepository;
  private final PlayerRepository playerRepository;

  /** Finds all expired temporary tokens in the database and removes them permanently. */
  @Transactional
  public void removeExpiredTokensFromDb() {
    final LocalDateTime now = LocalDateTime.now(UTC_ZONE_ID);

    final List<VerificationToken> expiredVerificationTokens =
        verificationTokenRepository.findAllByExpirationDateTimeBefore(now);

    final List<RefreshToken> expiredRefreshTokens =
        refreshTokenRepository.findAllByExpirationDateTimeBefore(now);

    final List<PasswordResetToken> expiredPasswordResetTokens =
        passwordResetTokenRepository.findAllByExpirationDateTimeBefore(now);

    final List<MagicLoginLinkToken> expiredMagicLoginLinkTokens =
        magicLinkLoginTokenRepository.findAllByExpirationDateTimeBefore(now);

    final int tokensCount =
        expiredVerificationTokens.size()
            + expiredRefreshTokens.size()
            + expiredMagicLoginLinkTokens.size()
            + expiredPasswordResetTokens.size();

    if (tokensCount > 0) {
      removeNotActivatedPlayers(expiredVerificationTokens);
      refreshTokenRepository.deleteAll(expiredRefreshTokens);
      passwordResetTokenRepository.deleteAll(expiredPasswordResetTokens);
      magicLinkLoginTokenRepository.deleteAll(expiredMagicLoginLinkTokens);
      log.info("Successfully removed {} expired tokens.", tokensCount);

    } else {
      log.info("No expired tokens to remove this time");
    }
  }

  private void removeNotActivatedPlayers(final List<VerificationToken> expiredVerificationTokens) {
    for (final VerificationToken token : expiredVerificationTokens) {
      final Player player = token.getPlayer();
      for (final Authority authority : player.getAuthorities()) {
        player.removeAuthority(authority);
      }
      verificationTokenRepository.delete(token);
      playerRepository.delete(player);
      log.info("Player [{}] has been removed", player.getUsername());
    }
  }
}
