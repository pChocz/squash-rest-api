package com.pj.squashrestapp.config.security.playerpasswordreset;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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
    final String subject = "Squash App - Reset Password";
    final String confirmationUrl = "http://localhost:8080" + event.getAppUrl() + "/players/resetPassword?token=" + token;

    final String beginning = """
            Hi,
            It seems that you have requested password reset for your account.
            To finish the process please click the following link and follow instructions:
                
            """;

    final String ending = """
            
            If it wasn't you, just ignore this email.
            Link will expire after 1h.
            Thanks for nothing!
            """;

    final String entireMessage = beginning + confirmationUrl + ending;

    emailSendConfig.sendEmail(receiver, subject, entireMessage);
  }

}
