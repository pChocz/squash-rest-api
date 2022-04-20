package com.pj.squashrestapp.hexagonal.email;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendEmailFacade {

    private final AccountActivationEmailService accountActivationEmailService;
    private final PasswordResetEmailService passwordResetEmailService;
    private final EmailChangeEmailService emailChangeEmailService;
    private final MagicLoginLinkEmailService magicLoginLinkEmailService;
    private final PlainEmailService plainEmailService;
    private final RecruiterLoggedInEmailService recruiterLoggedInEmailService;

    private final PlayerRepository playerRepository;

    public void sendAccountActivationEmail(
            final String receiverEmail, final String receiverName, final Locale locale, final String activationLink) {

        accountActivationEmailService.sendEmail(receiverEmail, receiverName, locale, activationLink);
    }

    public void sendPasswordResetEmail(
            final String receiverEmail,
            final String receiverName,
            final Locale locale,
            final String passwordResetLink) {

        passwordResetEmailService.sendEmail(receiverEmail, receiverName, locale, passwordResetLink);
    }

    public void sendEmailChangeEmail(
            final String receiverEmail, final String receiverName, final Locale locale, final String emailChangeLink) {

        emailChangeEmailService.sendEmail(receiverEmail, receiverName, locale, emailChangeLink);
    }

    public void sendMagicLoginLinkEmail(
            final String receiverEmail,
            final String receiverName,
            final Locale locale,
            final String passwordResetLink) {

        magicLoginLinkEmailService.sendEmail(receiverEmail, receiverName, locale, passwordResetLink);
    }

    public void sendPlainEmail(
            final String receiverEmail,
            final String receiverName,
            final Locale locale,
            final String subject,
            final String preheader,
            final String... contentLines) {

        plainEmailService.sendEmail(receiverEmail, receiverName, locale, subject, preheader, contentLines);
    }

    public void sendRecruiterLoggedInEmail() {
        recruiterLoggedInEmailService.sendEmail();
    }

    private boolean isModeratorOfLeague(final Player player) {
        return player.getRoles().stream().anyMatch(role -> role.getLeagueRole() == LeagueRole.MODERATOR);
    }
}
