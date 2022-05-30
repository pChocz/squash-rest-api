package com.pj.squashrestapp.hexagonal.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.pj.squashrestapp.hexagonal.email.EmailConstants.ADMIN_EMAIL_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.EMAIL_TEMPLATE;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.MY_WEBSITE_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.SQUASH_APP_HREF;

/** Service responsible for sending various types of emails to users of the app */
@Slf4j
@Service
@RequiredArgsConstructor
class PlainEmailService {

    private final SendEmailService sendEmailService;
    private final AbstractResourceBasedMessageSource messageSource;

    public void pushEmailToQueue(
            final String email,
            final String name,
            final Locale locale,
            final String subject,
            final String preheader,
            final String... linesOfText) {

        final Map<String, Object> model = new HashMap<>();

        // variables
        model.put("name", name);
        model.put("email", email);
        model.put("preheader", preheader);

        model.put("primaryContent", null);
        model.put("clickMessage", null);

        // static
        model.put("hiMessage", messageSource.getMessage("message.util.hi", new Object[] {name}, locale));

        final List<String> secondaryContent = Arrays.stream(linesOfText).collect(Collectors.toList());
        secondaryContent.add(
                messageSource.getMessage("message.util.doNotReply", new Object[] {ADMIN_EMAIL_HREF}, locale));

        model.put("secondaryContent", secondaryContent);

        model.put(
                "intendedFor",
                messageSource.getMessage("message.util.intendedFor", new Object[] {name, email}, locale));

        model.put(
                "devMessage",
                messageSource.getMessage("message.util.dev", new Object[] {SQUASH_APP_HREF, MY_WEBSITE_HREF}, locale));

        sendEmailService.pushEmailWithModelToQueue(email, subject, model, EMAIL_TEMPLATE);
    }
}
