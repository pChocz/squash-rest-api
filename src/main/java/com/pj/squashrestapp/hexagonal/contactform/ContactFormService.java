package com.pj.squashrestapp.hexagonal.contactform;

import com.pj.squashrestapp.hexagonal.email.EmailPrepareFacade;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static com.pj.squashrestapp.util.GeneralUtil.ADMIN_UUID;

/** Service responsible for handling messages from the contact form */
@Slf4j
@Service
@RequiredArgsConstructor
class ContactFormService {

    private final PlayerRepository playerRepository;
    private final EmailPrepareFacade emailPrepareFacade;

    void sendContactFormEmail(final String name, final String email, final String subject, final String message) {

        log.info("Contact form has been used");
        log.info("Name:    {}", name);
        log.info("Email:   {}", email);
        log.info("Subject: {}", subject);
        log.info("Message: \n{}", message);

        final Player admin = playerRepository.findByUuid(ADMIN_UUID);

        final String[] content =
                new String[] {"Name: " + name, "Email: " + email, "Subject: " + subject, "Message: " + message};

        emailPrepareFacade.pushPlainEmailToQueue(
                admin.getEmail(),
                admin.getUsername(),
                new Locale("en"),
                "Email from contact form of the squash-app.win",
                "Email from " + name,
                content);
    }
}
