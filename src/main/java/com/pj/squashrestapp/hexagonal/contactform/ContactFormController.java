package com.pj.squashrestapp.hexagonal.contactform;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/contact-form")
@RequiredArgsConstructor
class ContactFormController {

  private final ContactFormService service;

  @PostMapping(value = "/send")
  void sendPasswordResetEmail(
      @RequestParam final String name,
      @RequestParam final String email,
      @RequestParam final String subject,
      @RequestParam final String message) {

    service.sendContactFormEmail(name, email, subject, message);
  }
}
