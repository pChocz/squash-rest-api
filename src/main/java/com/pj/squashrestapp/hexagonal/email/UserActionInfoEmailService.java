package com.pj.squashrestapp.hexagonal.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.pj.squashrestapp.hexagonal.email.EmailConstants.EMAIL_TEMPLATE_USER_INFO;

@Slf4j
@Service
@RequiredArgsConstructor
class UserActionInfoEmailService {

    private final SendEmailService sendEmailService;
    private final EmailSendConfig emailSendConfig;

    void pushEmailToQueue(final Map<String, Object> model) {
        final String adminEmail = emailSendConfig.getAdminEmailAddress();
        sendEmailService.pushEmailWithModelToQueue(adminEmail, "User action information", model, EMAIL_TEMPLATE_USER_INFO);
    }
}
