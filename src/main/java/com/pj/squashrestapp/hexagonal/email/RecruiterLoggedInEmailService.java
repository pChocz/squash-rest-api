package com.pj.squashrestapp.hexagonal.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.pj.squashrestapp.hexagonal.email.EmailConstants.EMAIL_TEMPLATE;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.MY_WEBSITE_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.SQUASH_APP_HREF;

@Slf4j
@Service
@RequiredArgsConstructor
class RecruiterLoggedInEmailService {

    private final SendEmailService sendEmailService;
    private final EmailSendConfig emailSendConfig;
    private final AbstractResourceBasedMessageSource messageSource;

    void pushEmailToQueue(final String ip) {
        final String adminEmail = emailSendConfig.getAdminEmailAddress();
        final Locale locale = new Locale("en");

        final Map<String, Object> model = new HashMap<>();
        model.put("preheader", "Recruiter has logged in!");
        model.put("hiMessage", messageSource.getMessage("message.util.hi", new Object[] {"Admin"}, locale));
        model.put("primaryContent", null);
        model.put("clickMessage", null);
        model.put("secondaryContent", new Object[] {"Recruiter has logged in! IP: " + ip});
        model.put(
                "intendedFor",
                messageSource.getMessage("message.util.intendedFor", new Object[] {"Admin", adminEmail}, locale));
        model.put(
                "devMessage",
                messageSource.getMessage("message.util.dev", new Object[] {SQUASH_APP_HREF, MY_WEBSITE_HREF}, locale));

        sendEmailService.pushEmailWithModelToQueue(adminEmail, "Recruiter login", model, EMAIL_TEMPLATE);
    }
}
