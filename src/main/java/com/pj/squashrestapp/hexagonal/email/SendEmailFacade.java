package com.pj.squashrestapp.hexagonal.email;

import java.util.Locale;
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
}
