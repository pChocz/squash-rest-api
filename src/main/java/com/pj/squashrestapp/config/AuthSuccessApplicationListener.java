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
        if (username.equalsIgnoreCase("RECRUITER")) {
            final String ip1 = AuthorizationUtil.extractRequestIpAddress1();
            final String ip2 = AuthorizationUtil.extractRequestIpAddress2();
            final String ip3 = AuthorizationUtil.extractRequestIpAddress3();
            final String ips = ip1 + " | " + ip2 + " | " + ip3;
            log.info("Recruiter login from ip: {}", ips);
            emailPrepareFacade.pushRecruiterLoggedInEmailToQueue(ips);
        }
    }
}
