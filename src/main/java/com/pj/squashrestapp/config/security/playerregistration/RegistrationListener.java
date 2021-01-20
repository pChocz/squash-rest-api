package com.pj.squashrestapp.config.security.playerregistration;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.config.email.EmailTemplate;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 *
 */
@Component
@AllArgsConstructor
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

  private final PlayerService playerService;
  private final EmailSendConfig emailSendConfig;

  @Override
  public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
    this.confirmRegistration(event);
  }

  private void confirmRegistration(final OnRegistrationCompleteEvent event) {
    final String token = UUID.randomUUID().toString();
    final Player player = event.getPlayer();
    playerService.createAndPersistVerificationToken(token, player);

    final String receiver = player.getEmail();
    final String subject = "Confirm registration";
    final String confirmationUrl = event.getFrontendUrl() + "confirm-registration/" + token;

    final String htmlContent = EmailTemplate.builder()
            .title(subject)
            .username(player.getUsername())
            .buttonLabel("Confirm Registration")
            .buttonLink(confirmationUrl)
            .beginContent("""
                    It seems that you have created an account in our App.
                    <br>To finish the registration process please click the following link:  
                    """)
            .endContent("""
                    If it wasn't you, just ignore this email. 
                    <br>Link will expire after 24h and the account will be deleted.
                    """)
            .build()
            .createHtmlContent();

    emailSendConfig.sendEmail(receiver, subject, htmlContent);
  }

}
