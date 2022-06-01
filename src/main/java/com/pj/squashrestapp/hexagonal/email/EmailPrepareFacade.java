package com.pj.squashrestapp.hexagonal.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailPrepareFacade {

    private final AccountActivationEmailService accountActivationEmailService;
    private final PasswordResetEmailService passwordResetEmailService;
    private final EmailChangeEmailService emailChangeEmailService;
    private final MagicLoginLinkEmailService magicLoginLinkEmailService;
    private final PlainEmailService plainEmailService;
    private final UserActionInfoEmailService userActionInfoEmailService;
    private final ExceptionEmailService exceptionEmailService;

    public void pushAccountActivationEmailToQueue(
            final String receiverEmail, final String receiverName, final Locale locale, final String activationLink) {

        accountActivationEmailService.pushEmailToQueue(receiverEmail, receiverName, locale, activationLink);
    }

    public void pushPasswordResetEmailToQueue(
            final String receiverEmail,
            final String receiverName,
            final Locale locale,
            final String passwordResetLink) {

        passwordResetEmailService.pushEmailToQueue(receiverEmail, receiverName, locale, passwordResetLink);
    }

    public void pushEmailChangeEmailToQueue(
            final String receiverEmail, final String receiverName, final Locale locale, final String emailChangeLink) {

        emailChangeEmailService.pushEmailToQueue(receiverEmail, receiverName, locale, emailChangeLink);
    }

    public void pushMagicLoginLinkEmailToQueue(
            final String receiverEmail,
            final String receiverName,
            final Locale locale,
            final String passwordResetLink) {

        magicLoginLinkEmailService.pushEmailToQueue(receiverEmail, receiverName, locale, passwordResetLink);
    }

    public void pushPlainEmailToQueue(
            final String receiverEmail,
            final String receiverName,
            final Locale locale,
            final String subject,
            final String preheader,
            final String... contentLines) {

        plainEmailService.pushEmailToQueue(receiverEmail, receiverName, locale, subject, preheader, contentLines);
    }

    public void pushUserActionInfoEmailToQueue(final Map<String, Object> content) {
        userActionInfoEmailService.pushEmailToQueue(content);
    }

    public void pushExceptionEmailToQueue(final Map<String, Object> content) {
        exceptionEmailService.pushEmailToQueue(content);
    }

}
