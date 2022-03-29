package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.EmailChangeToken;
import com.pj.squashrestapp.model.MagicLoginLinkToken;
import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RefreshToken;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.repository.EmailChangeTokenRepository;
import com.pj.squashrestapp.repository.MagicLinkLoginTokenRepository;
import com.pj.squashrestapp.repository.PasswordResetTokenRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RefreshTokenRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRemovalService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;
    private final MagicLinkLoginTokenRepository magicLinkLoginTokenRepository;
    private final PlayerRepository playerRepository;

    /** Finds all expired temporary tokens in the database and removes them permanently. */
    @Transactional
    public void removeExpiredTokensFromDb() {
        final LocalDateTime now = LocalDateTime.now(UTC_ZONE_ID);

        final List<VerificationToken> expiredVerificationTokens =
                verificationTokenRepository.findAllByExpirationDateTimeBefore(now);

        final List<RefreshToken> expiredRefreshTokens = refreshTokenRepository.findAllByExpirationDateTimeBefore(now);

        final List<PasswordResetToken> expiredPasswordResetTokens =
                passwordResetTokenRepository.findAllByExpirationDateTimeBefore(now);

        final List<EmailChangeToken> expiredEmailChangeTokens =
                emailChangeTokenRepository.findAllByExpirationDateTimeBefore(now);

        final List<MagicLoginLinkToken> expiredMagicLoginLinkTokens =
                magicLinkLoginTokenRepository.findAllByExpirationDateTimeBefore(now);

        final int tokensCount = expiredVerificationTokens.size()
                + expiredRefreshTokens.size()
                + expiredMagicLoginLinkTokens.size()
                + expiredEmailChangeTokens.size()
                + expiredPasswordResetTokens.size();

        if (tokensCount > 0) {
            removeNotActivatedPlayers(expiredVerificationTokens);
            refreshTokenRepository.deleteAll(expiredRefreshTokens);
            passwordResetTokenRepository.deleteAll(expiredPasswordResetTokens);
            emailChangeTokenRepository.deleteAll(expiredEmailChangeTokens);
            magicLinkLoginTokenRepository.deleteAll(expiredMagicLoginLinkTokens);
            log.info("Successfully removed {} expired tokens.", tokensCount);

        } else {
            log.info("No expired tokens to remove this time");
        }
    }

    /**
     * Removes non-enabled players for which verification token has already expired.
     */
    private void removeNotActivatedPlayers(final List<VerificationToken> expiredVerificationTokens) {
        for (final VerificationToken token : expiredVerificationTokens) {
            final Player player = token.getPlayer();
            if (!player.isEnabled()) {
                for (final Authority authority : player.getAuthorities()) {
                    player.removeAuthority(authority);
                }
                playerRepository.delete(player);
            }
            verificationTokenRepository.delete(token);
            log.info("Player [{}] has been removed", player.getUsername());
        }
    }
}
