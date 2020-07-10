package com.pj.squashrestapp.config.security.playerregistration;

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
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

  @Autowired
  private PlayerService playerService;

  @Autowired
  private EmailSendConfig emailSendConfig;

  @Override
  public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
    this.confirmRegistration(event);
  }

  private void confirmRegistration(final OnRegistrationCompleteEvent event) {
    final String token = UUID.randomUUID().toString();
    final Player player = event.getPlayer();
    playerService.createAndPersistVerificationToken(token, player);

    final String receiver = player.getEmail();
    final String subject = "Squash App - Confirm Registration";
    final String confirmationUrl = "http://localhost:8080" + event.getAppUrl() + "/players/confirmRegistration?token=" + token;

    final String beginning = """
            Hi,
            It seems that you have created an account in our App.
            To finish the registration process please click the following link:
                
            """;

    final String ending = """
            
            If it wasn't you, just ignore this email. 
            Link will expire after 24h.            
            Thanks for nothing!
            """;

    final String entireMessage = beginning + confirmationUrl + ending;

    emailSendConfig.sendEmail(receiver, subject, entireMessage);
  }

}
