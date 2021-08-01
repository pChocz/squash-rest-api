package com.pj.squashrestapp.hexagonal.email;

import static com.pj.squashrestapp.hexagonal.email.EmailConstants.ADMIN_EMAIL_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.MY_WEBSITE_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.SQUASH_APP_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.TEMPLATE_PLAIN;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.stereotype.Service;

/** Service responsible for sending various types of emails to users of the app */
@Slf4j
@Service
@RequiredArgsConstructor
class PlainEmailService {

  private final SendEmailService sendEmailService;
  private final AbstractResourceBasedMessageSource messageSource;

  public void sendEmail(
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

    // todo: verify how to implement such array
    model.put("contentLines", linesOfText);

    // static
    model.put(
        "hiMessage", messageSource.getMessage("message.util.hi", new Object[] {name}, locale));

    model.put(
        "doNotReplyMessage",
        messageSource.getMessage(
            "message.util.doNotReply", new Object[] {ADMIN_EMAIL_HREF}, locale));
    model.put(
        "intendedFor",
        messageSource.getMessage("message.util.intendedFor", new Object[] {name, email}, locale));
    model.put(
        "devMessage",
        messageSource.getMessage(
            "message.util.dev", new Object[] {SQUASH_APP_HREF, MY_WEBSITE_HREF}, locale));

    sendEmailService.sendEmailWithModel(email, subject, model, TEMPLATE_PLAIN);
  }
}
