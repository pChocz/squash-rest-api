package com.pj.squashrestapp.hexagonal.email;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
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
      final String templateString) {

    try {
      final Template template = freemarkerConfiguration.getTemplate(templateString);
      final String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
      emailSendConfig.sendEmailWithHtmlContent(email, subject, content);

    } catch (final IOException | TemplateException e) {
      log.error("Template problem - {}", templateString, e);
    }
  }

}
