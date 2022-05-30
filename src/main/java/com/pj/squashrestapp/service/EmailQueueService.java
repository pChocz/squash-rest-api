package com.pj.squashrestapp.service;

import com.pj.squashrestapp.hexagonal.email.EmailSendConfig;
import com.pj.squashrestapp.model.Email;
import com.pj.squashrestapp.repository.EmailQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailQueueService {

    private static final int MAX_TRIES_COUNT = 10;
    private static final int MAX_NUMBER_OF_DAYS_TO_KEEP_EMAILS = 7;

    private final EmailQueueRepository emailQueueRepository;
    private final EmailSendConfig emailSendConfig;

    public void pushToQueue(Email email) {
        emailQueueRepository.save(email);
    }

    public void processUnsentEmails() {
        final List<Email> emails = emailQueueRepository.findEmailsToSend(LocalDateTime.now(), MAX_TRIES_COUNT);
        int sentEmailCounter = 0;
        for (final Email email : emails) {
            final String toAddress = email.getToAddress();
            final String subject = email.getSubject();
            final String htmlContent = email.getHtmlContent();
            final int triesCount = email.getTriesCount();
            final boolean emailSent = emailSendConfig.sendEmailWithHtmlContent(toAddress, subject, htmlContent);
            email.setTriesCount(triesCount + 1);
            if (emailSent) {
                sentEmailCounter++;
                email.setSent(true);
                email.setSentDatetime(LocalDateTime.now());
            }
        }
        emailQueueRepository.saveAll(emails);
        if (sentEmailCounter > 0) {
            log.info("Sent {} email(s)", sentEmailCounter);
        }
    }

    public void deleteOldEmails() {
        final LocalDateTime threshold = LocalDateTime.now().minusDays(MAX_NUMBER_OF_DAYS_TO_KEEP_EMAILS);
        final List<Email> emails = emailQueueRepository.findEmailsExceedingThreshold(threshold);
        emailQueueRepository.deleteAll(emails);
        if (!emails.isEmpty()) {
            log.info("Removed {} email(s)", emails.size());
        }
    }
}
