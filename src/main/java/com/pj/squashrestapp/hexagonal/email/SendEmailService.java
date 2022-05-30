package com.pj.squashrestapp.hexagonal.email;

import com.pj.squashrestapp.model.Email;
import com.pj.squashrestapp.service.EmailQueueService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/** Service responsible for sending various types of emails to users of the app */
@Slf4j
@Service
@RequiredArgsConstructor
class SendEmailService {

    private final Configuration freemarkerConfiguration;
    private final EmailQueueService emailQueueService;

    void pushEmailWithModelToQueue(
            final String receiverAddress,
            final String subject,
            final Map<String, Object> model,
            final String templateString) {

        try {
            final Template template = freemarkerConfiguration.getTemplate(templateString);
            final String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            final Email emailToSend = new Email();
            emailToSend.setSent(false);
            emailToSend.setToAddress(receiverAddress);
            emailToSend.setSubject(subject);
            emailToSend.setHtmlContent(content);
            emailToSend.setSendAfterDatetime(LocalDateTime.now());
            emailToSend.setSendBeforeDatetime(LocalDateTime.now().plusMinutes(15));
            emailToSend.setTriesCount(0);

            emailQueueService.pushToQueue(emailToSend);

        } catch (final IOException | TemplateException e) {
            log.error("Template problem - {}", templateString, e);
        }
    }
}
