package com.pj.squashrestapp.hexagonal.email;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Locale;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
class EmailController {

  private final SendEmailFacade facade;

  @GetMapping(value = "/send-password-reset-email")
  @PreAuthorize("isAdmin()")
  void sendPasswordResetEmail(
      @RequestParam(defaultValue = "en") final String lang,
      @RequestParam final String email,
      @RequestParam final String name,
      @RequestParam final String passwordResetLink)
      throws IOException, MessagingException, TemplateException {

    facade.sendPasswordResetEmail(email, name, new Locale(lang), passwordResetLink);
  }

  @GetMapping(value = "/send-activation-link-email")
  @PreAuthorize("isAdmin()")
  void sendAccountActivationEmail(
      @RequestParam(defaultValue = "en") final String lang,
      @RequestParam final String email,
      @RequestParam final String name,
      @RequestParam final String activationLink)
      throws IOException, MessagingException, TemplateException {

    facade.sendAccountActivationEmail(email, name, new Locale(lang), activationLink);
  }

  @GetMapping(value = "/send-plain-email")
  @PreAuthorize("isAdmin()")
  void sendPlainEmail(
      @RequestParam(defaultValue = "en") final String lang,
      @RequestParam final String email,
      @RequestParam final String name,
      @RequestParam final String subject,
      @RequestParam final String preheader,
      @RequestParam final String... contentLines)
      throws IOException, MessagingException, TemplateException {

    facade.sendPlainEmail(email, name, new Locale(lang), subject, preheader, contentLines);
  }

  @GetMapping(value = "/send-recruiter-login-info")
  @PreAuthorize("isAdmin()")
  void sendRecruiterLoginInfo(final HttpServletRequest request) {
    facade.sendRecruiterLoggedInEmail();
  }
}