package com.pj.squashrestapp.config.security.playerpasswordreset;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.config.email.EmailTemplate;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 *
 */
@Component
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {

  @Autowired
  private PlayerService playerService;

  @Autowired
  private EmailSendConfig emailSendConfig;

  @Override
  public void onApplicationEvent(final OnPasswordResetEvent event) {
    this.confirmPasswordReset(event);
  }

  private void confirmPasswordReset(final OnPasswordResetEvent event) {
    final String token = UUID.randomUUID().toString();
    final Player player = event.getPlayer();
    playerService.createAndPersistPasswordResetToken(token, player);

    final String receiver = player.getEmail();
    final String subject = "Password reset request";
    final String passwordResetUrl = "http://localhost:8080" + event.getAppUrl() + "/players/resetPassword?token=" + token;

    final String htmlContent = EmailTemplate.builder()
            .title(subject)
            .buttonLabel("Reset Password")
            .buttonLink(passwordResetUrl)
            .beginContent("""
                    Hi,
                    <br/>It seems that you have requested password reset for your account.
                    <br/>To finish the process please click the following link and follow on-screen instructions.
                    """)
            .endContent("""
                    If it wasn't you, just ignore this email.
                    <br/>Link will expire after 1h.
                    """)
            .build()
            .createHtmlContent();

    emailSendConfig.sendEmail(receiver, subject, htmlContent);
  }

}
