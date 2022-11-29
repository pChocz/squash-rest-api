package com.pj.squashrestapp.hexagonal.contactform;

import com.pj.squashrestapp.hexagonal.email.EmailPrepareFacade;
import com.pj.squashrestapp.hexagonal.email.EmailSendConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;


/** Service responsible for handling messages from the contact form */
@Slf4j
@Service
@RequiredArgsConstructor
class ContactFormService {

    private final EmailPrepareFacade emailPrepareFacade;
    private final EmailSendConfig emailSendConfig;

    void sendContactFormEmail(final String name, final String email, final String subject, final String message) {
        final String adminEmail = emailSendConfig.getAdminEmailAddress();

        final String[] content =
                new String[] {"Name: " + name, "Email: " + email, "Subject: " + subject, "Message: " + message};

        emailPrepareFacade.pushPlainEmailToQueue(
                adminEmail,
                "Admin",
                new Locale("en"),
                "Email from contact form of the squash-app.win",
                "Email from " + name,
                content);
    }
}
