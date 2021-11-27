package com.pj.squashrestapp.hexagonal.email;

import static com.pj.squashrestapp.hexagonal.email.EmailConstants.ADMIN_EMAIL_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.EMAIL_TEMPLATE;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.MY_WEBSITE_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.SQUASH_APP_HREF;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class MagicLoginLinkEmailService {

  private final SendEmailService sendEmailService;
  private final AbstractResourceBasedMessageSource messageSource;

  void sendEmail(
      final String email, final String name, final Locale locale, final String passwordResetLink) {

    final String preheader =
        messageSource.getMessage("message.magicLoginLink.preheader", null, locale);
    final String subject = messageSource.getMessage("message.magicLoginLink.subject", null, locale);

    final Map<String, Object> model = new HashMap<>();

    // variables
    model.put("name", name);
    model.put("email", email);
    model.put("preheader", preheader);
    model.put("link", passwordResetLink);

    // static
    model.put(
        "hiMessage", messageSource.getMessage("message.util.hi", new Object[] {name}, locale));

    model.put(
        "primaryContent",
        messageSource.getMessage("message.magicLoginLink.youHaveRequested", null, locale));

    model.put(
        "clickMessage", messageSource.getMessage("message.magicLoginLink.buttonText", null, locale));

    model.put("copyLinkMessage", messageSource.getMessage("message.util.copyLink", null, locale));

    model.put(
        "secondaryContent",
        new String[] {
          messageSource.getMessage("message.magicLoginLink.expirationNote", null, locale),
          messageSource.getMessage(
              "message.util.doNotReply", new Object[] {ADMIN_EMAIL_HREF}, locale),
        });

    model.put(
        "intendedFor",
        messageSource.getMessage("message.util.intendedFor", new Object[] {name, email}, locale));

    model.put(
        "devMessage",
        messageSource.getMessage(
            "message.util.dev", new Object[] {SQUASH_APP_HREF, MY_WEBSITE_HREF}, locale));

    sendEmailService.sendEmailWithModel(email, subject, model, EMAIL_TEMPLATE);
  }
}
