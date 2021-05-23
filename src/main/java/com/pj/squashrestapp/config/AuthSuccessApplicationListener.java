package com.pj.squashrestapp.config;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.config.email.EmailTemplate;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

/** Just sending an email about recruiter login to the admin */
@Slf4j
@AllArgsConstructor
public class AuthSuccessApplicationListener
    implements ApplicationListener<AuthenticationSuccessEvent> {

  private static final UUID ADMIN_UUID = UUID.fromString("73992a9c-fea3-4a24-a95b-91e1e840c26a");

  private final EmailSendConfig emailSendConfig;
  private final PlayerRepository playerRepository;

  @Override
  public void onApplicationEvent(final AuthenticationSuccessEvent appEvent) {
    final UserDetailsImpl principal = (UserDetailsImpl) appEvent.getAuthentication().getPrincipal();
    final String username = principal.getUsername();
    if (username.equalsIgnoreCase("RECRUITER")) {
      sendEmailToAdmin();
    }
  }

  private void sendEmailToAdmin() {
    final long startTime = System.nanoTime();
    final Player admin = playerRepository.findByUuid(ADMIN_UUID);
    final String receiver = admin.getEmail();
    final String subject = "Recruiter login";

    final String htmlContent =
        EmailTemplate.builder()
            .isWithButton(false)
            .title(subject)
            .username("Admin")
            .beginContent(
                "It seems that recruiter account has just been used to log into the Squash App!")
            .endContent("Just informing, no action required.")
            .build()
            .createHtmlContent();

    emailSendConfig.sendEmail(receiver, subject, htmlContent);
    log.info(
        "Email has been sent to ADMIN and it took {} s",
        GeneralUtil.getDurationSecondsRounded(startTime));
  }
}
