package com.pj.squashrestapp.hexagonal.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.EMAIL_TEMPLATE_EXCEPTION;

@Slf4j
@Service
@RequiredArgsConstructor
class ExceptionEmailService {

    private final SendEmailService sendEmailService;
    private final EmailSendConfig emailSendConfig;

    void pushEmailToQueue(final Map<String, Object> model) {
        final String adminEmail = emailSendConfig.getAdminEmailAddress();
        sendEmailService.pushEmailWithModelToQueue(adminEmail, "Error has occurred", model, EMAIL_TEMPLATE_EXCEPTION);
    }
}
