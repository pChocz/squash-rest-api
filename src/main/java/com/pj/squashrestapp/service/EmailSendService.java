package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/** Service responsible for sending various types of emails to users of the app */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendService {

  private static final String TEMPLATE_WITH_BUTTON = "email_template_button.html";
  private static final String TEMPLATE_PLAIN = "email_template_plain.html";

  private static final String ADMIN_EMAIL_HREF =
      "<a href =\"mailto: admin@squash-app.win\" style=\"color: #0000EE;\">admin@squash-app.win</a>";

  private static final String SQUASH_APP_HREF =
      "<a href=\"https://squash-app.win\" style=\"color: #0000EE;\">Squash App</a>";

  private static final String MY_WEBSITE_HREF =
      "<a href=\"https://www.choczynski.pl\" style=\"color: #0000EE;\">Piotr Choczyński</a>";

  private final Configuration freemarkerConfiguration;
  private final EmailSendConfig emailSendConfig;
  private final AbstractResourceBasedMessageSource messageSource;

  public void sendPlainEmail(
      final String email,
      final String name,
      final String lang,
      final String subject,
      final String preheader,
      final String... linesOfText) {

    final Locale locale = new Locale(lang);

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
        messageSource.getMessage(
            "message.util.intendedFor", new Object[] {name, email}, locale));
    model.put(
        "devMessage",
        messageSource.getMessage("message.util.dev", new Object[] {SQUASH_APP_HREF, MY_WEBSITE_HREF}, locale));

    sendEmailWithModel(email, subject, model, TEMPLATE_PLAIN);
  }

  public void sendAccountActivationEmail(
      final String email,
      final String name,
      final String lang,
      final String subject,
      final String preheader,
      final String activationLink) {

    //    messageSource.setDefaultEncoding("UTF-8");

    final Locale locale = new Locale(lang);

    final Map<String, Object> model = new HashMap<>();

    // variables
    model.put("name", name);
    model.put("email", email);
    model.put("preheader", preheader);
    model.put("activationLink", activationLink);

    // static
    model.put(
        "hiMessage", messageSource.getMessage("message.util.hi", new Object[] {name}, locale));
    model.put(
        "youHaveCreatedAnAccountMessage",
        messageSource.getMessage("message.activation.youHaveCreatedAnAccount", null, locale));
    model.put(
        "clickToActivateMessage",
        messageSource.getMessage("message.activation.buttonText", null, locale));
    model.put(
        "copyLinkMessage", messageSource.getMessage("message.activation.copyLink", null, locale));
    model.put("ignoreMessage", messageSource.getMessage("message.util.ignore", null, locale));
    model.put(
        "doNotReplyMessage",
        messageSource.getMessage(
            "message.util.doNotReply", new Object[] {ADMIN_EMAIL_HREF}, locale));
    model.put(
        "intendedFor",
        messageSource.getMessage(
            "message.util.intendedFor", new Object[] {name, email}, locale));
    model.put(
        "devMessage",
        messageSource.getMessage("message.util.dev", new Object[] {SQUASH_APP_HREF, MY_WEBSITE_HREF}, locale));

    sendEmailWithModel(email, subject, model, TEMPLATE_WITH_BUTTON);
  }

  private void sendEmailWithModel(
      final String email,
      final String subject,
      final Map<String, Object> model,
      final String template) {

    try {

      final String content =
          FreeMarkerTemplateUtils.processTemplateIntoString(
              freemarkerConfiguration.getTemplate(template), model);

      emailSendConfig.sendEmailTest(email, subject, content);

    } catch (final IOException | TemplateException e) {
      log.error("Template problem - {}", template, e);

    } catch (final MessagingException e) {
      log.error("Message send problem - {}", email, e);
    }
  }
}
