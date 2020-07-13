package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.BlacklistedToken;
import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.repository.BlacklistedTokenRepository;
import com.pj.squashrestapp.repository.PasswordResetTokenRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/**
 *
 */
@Slf4j
@Service
public class ExpiredTokenService {

  @Autowired
  private BlacklistedTokenRepository blacklistedTokenRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;


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
      log.info("Succesfully removed {} expried tokens.", tokensCount);

    } else {
      log.info("No expired tokens to remove this time");
    }
  }

}
