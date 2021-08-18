package com.pj.squashrestapp.hexagonal.email;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.service.PlayerService;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmailFacade {

  private final AccountActivationEmailService accountActivationEmailService;
  private final PasswordResetEmailService passwordResetEmailService;
  private final PlainEmailService plainEmailService;
  private final RecruiterLoggedInEmailService recruiterLoggedInEmailService;

  private final PlayerRepository playerRepository;


  public void sendAccountActivationEmail(
      final String receiverEmail,
      final String receiverName,
      final Locale locale,
      final String activationLink) {

    accountActivationEmailService.sendEmail(
        receiverEmail, receiverName, locale, activationLink);
  }

  public void sendPasswordResetEmail(
      final String receiverEmail,
      final String receiverName,
      final Locale locale,
      final String passwordResetLink) {

    passwordResetEmailService.sendEmail(
        receiverEmail, receiverName, locale, passwordResetLink);
  }

  public void sendPlainEmail(
      final String receiverEmail,
      final String receiverName,
      final Locale locale,
      final String subject,
      final String preheader,
      final String... contentLines) {

    plainEmailService.sendEmail(
        receiverEmail, receiverName, locale, subject, preheader, contentLines);
  }

  public void sendRecruiterLoggedInEmail() {
    recruiterLoggedInEmailService.sendEmail();
  }

  private boolean isModeratorOfLeague(final Player player) {
    return player.getRoles().stream()
        .anyMatch(role -> role.getLeagueRole() == LeagueRole.MODERATOR);
  }
}
