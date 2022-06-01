package com.pj.squashrestapp.config;

import com.pj.squashrestapp.hexagonal.email.EmailPrepareFacade;
import com.pj.squashrestapp.util.AuthorizationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import java.util.HashMap;
import java.util.Map;

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
            final Map<String, Object> model = new HashMap<>();
            model.put("preheader", "Recruiter login");
            model.put("info", "Recruiter has logged in!");
            model.put("user", username);
            model.put("ip", ipAddress);
            emailPrepareFacade.pushUserActionInfoEmailToQueue(model);
        }
    }
}
