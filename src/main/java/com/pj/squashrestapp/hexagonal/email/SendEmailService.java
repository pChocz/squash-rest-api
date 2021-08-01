package com.pj.squashrestapp.hexagonal.email;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/** Service responsible for sending various types of emails to users of the app */
@Slf4j
@Service
@RequiredArgsConstructor
class SendEmailService {

  private final Configuration freemarkerConfiguration;
  private final EmailSendConfig emailSendConfig;

  void sendEmailWithModel(
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
