package com.pj.squashrestapp.config;

import com.pj.squashrestapp.hexagonal.email.SendEmailFacade;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

/** Just sending an email about recruiter login to the admin */
@Slf4j
@AllArgsConstructor
public class AuthSuccessApplicationListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final SendEmailFacade sendEmailFacade;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent appEvent) {
        final UserDetailsImpl principal =
                (UserDetailsImpl) appEvent.getAuthentication().getPrincipal();
        final String username = principal.getUsername();
        if (username.equalsIgnoreCase("RECRUITER")) {
            final long startTime = System.nanoTime();
            sendEmailFacade.sendRecruiterLoggedInEmail();
            log.info("Email has been sent to ADMIN and it took {} s", GeneralUtil.getDurationSecondsRounded(startTime));
        }
    }
}
