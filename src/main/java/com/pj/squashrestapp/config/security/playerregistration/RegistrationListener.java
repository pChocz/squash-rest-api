package com.pj.squashrestapp.config.security.playerregistration;

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
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

  private final PlayerService playerService;
  private final EmailSendConfig emailSendConfig;

  @Override
  public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
    this.confirmRegistration(event);
  }

  private void confirmRegistration(final OnRegistrationCompleteEvent event) {
    final UUID token = UUID.randomUUID();
    final Player player = event.getPlayer();
    playerService.createAndPersistVerificationToken(token, player);

    final String receiver = player.getEmail();
    final String subject = "Confirm registration";
    final String confirmationUrl = event.getFrontendUrl() + "confirm-registration/" + token;

    final String htmlContent = EmailTemplate.builder()
            .isWithButton(true)
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
