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
public class TokenService {

  @Autowired
  private BlacklistedTokenRepository blacklistedTokenRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired
  private PlayerRepository playerRepository;


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

}

















