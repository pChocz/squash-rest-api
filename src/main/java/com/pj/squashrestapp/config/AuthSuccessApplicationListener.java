package com.pj.squashrestapp.config;

import com.pj.squashrestapp.hexagonal.email.EmailPrepareFacade;
import com.pj.squashrestapp.util.AuthorizationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

/** Just sending an email about recruiter login to the admin */
@Slf4j
@AllArgsConstructor
public class AuthSuccessApplicationListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final EmailPrepareFacade emailPrepareFacade;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent appEvent) {
        final UserDetailsImpl principal = (UserDetailsImpl) appEvent.getAuthentication().getPrincipal();
        final String username = principal.getUsername();
        final String ipAddress = AuthorizationUtil.extractRequestIpAddress();
        log.info("{} has logged in from ip {}", username, ipAddress);
        if (username.equalsIgnoreCase("RECRUITER")) {
            emailPrepareFacade.pushRecruiterLoggedInEmailToQueue(ipAddress);
        }
    }
}
