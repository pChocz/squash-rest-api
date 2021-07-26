package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.service.EmailSendService;
import freemarker.template.TemplateException;
import java.io.IOException;
import javax.mail.MessagingException;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

  private final EmailSendService emailSendService;

  @GetMapping(value = "/sendActivationLinkEmail")
  @PreAuthorize("isAdmin()")
  void sendAccountActivationEmail(
      @RequestParam final String email,
      @RequestParam final String name,
      @RequestParam final String lang,
      @RequestParam final String subject,
      @RequestParam final String preheader,
      @RequestParam final String activationLink)
      throws IOException, MessagingException, TemplateException {

    emailSendService.sendAccountActivationEmail(
        email, name, lang, subject, preheader, activationLink);
  }

  @GetMapping(value = "/sendGenericEmail")
  @PreAuthorize("isAdmin()")
  void sendGenericContentEmail(
      @RequestParam final String email,
      @RequestParam final String name,
      @RequestParam final String lang,
      @RequestParam final String subject,
      @RequestParam final String preheader,
      @RequestParam final String... contentLines)
      throws IOException, MessagingException, TemplateException {

    emailSendService.sendPlainEmail(email, name, lang, subject, preheader, contentLines);
  }
}
