package com.pj.squashrestapp.config.security.playerpasswordreset;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.config.email.EmailTemplate;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.service.PlayerService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@AllArgsConstructor
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {

  private final PlayerService playerService;
  private final EmailSendConfig emailSendConfig;

  @Override
  public void onApplicationEvent(final OnPasswordResetEvent event) {
    this.confirmPasswordReset(event);
  }

  private void confirmPasswordReset(final OnPasswordResetEvent event) {
    final UUID token = UUID.randomUUID();
    final Player player = event.getPlayer();
    playerService.createAndPersistPasswordResetToken(token, player);

    final String receiver = player.getEmail();
    final String subject = "Password reset request";
    final String passwordResetUrl = event.getFrontendUrl() + "reset-password/" + token;

    final String htmlContent = EmailTemplate.builder()
            .isWithButton(true)
            .title(subject)
            .username(player.getUsername())
            .buttonLabel("Reset Password")
            .buttonLink(passwordResetUrl)
            .beginContent("""
                    It seems that you have requested password reset for your account.
                    <br>To finish the process please click the following link and follow on-screen instructions.
                    """)
            .endContent("""
                    If it wasn't you, just ignore this email.
                    <br>Link will expire after 1h.
                    """)
            .build()
            .createHtmlContent();

    emailSendConfig.sendEmail(receiver, subject, htmlContent);
  }

}
