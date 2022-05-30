package com.pj.squashrestapp.hexagonal.email;

import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.EMAIL_TEMPLATE;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.MY_WEBSITE_HREF;
import static com.pj.squashrestapp.hexagonal.email.EmailConstants.SQUASH_APP_HREF;
import static com.pj.squashrestapp.util.GeneralUtil.ADMIN_UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class ExceptionEmailService {

    private final SendEmailService sendEmailService;
    private final PlayerRepository playerRepository;
    private final AbstractResourceBasedMessageSource messageSource;

    void pushEmailToQueue(final List<String> content) {
        final Player admin = playerRepository.findByUuid(ADMIN_UUID);
        final String email = admin.getEmail();

        final Locale locale = new Locale("en");

        final Map<String, Object> model = new HashMap<>();
        model.put("preheader", "An error has occurred!");
        model.put("hiMessage", null);
        model.put("primaryContent", null);
        model.put("clickMessage", null);

        model.put("secondaryContent", content);

        model.put(
                "intendedFor",
                messageSource.getMessage("message.util.intendedFor", new Object[] {admin, email}, locale));

        model.put(
                "devMessage",
                messageSource.getMessage("message.util.dev", new Object[] {SQUASH_APP_HREF, MY_WEBSITE_HREF}, locale));

        sendEmailService.pushEmailWithModelToQueue(email, "ERROR", model, EMAIL_TEMPLATE);
    }
}
