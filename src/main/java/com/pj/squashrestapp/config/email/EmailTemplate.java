package com.pj.squashrestapp.config.email;

import com.pj.squashrestapp.config.security.playerpasswordreset.PasswordResetListener;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
@Slf4j
@Builder
public class EmailTemplate {

  private final static String TEMPLATE_PATH = "templates" + File.separator + "email_template.html";

  private final static String TITLE_PLACEHOLDER = "_Title_";
  private final static String BUTTON_LABEL_PLACEHOLDER = "_Button_label_";
  private final static String BUTTON_LINK_PLACEHOLDER = "_Button_link_";
  private final static String BEGIN_CONTENT_PLACEHOLDER = "_Begin_";
  private final static String END_CONTENT_PLACEHOLDER = "_End_";

  private final String title;
  private final String buttonLabel;
  private final String buttonLink;
  private final String beginContent;
  private final String endContent;

  public final String createHtmlContent() {
    final ClassPathResource classPathResource = new ClassPathResource(
            TEMPLATE_PATH,
            this.getClass().getClassLoader());

    String entireMessage = "";
    try (final InputStream inputStream = classPathResource.getInputStream()) {
      entireMessage = new String(inputStream.readAllBytes());
    } catch (final IOException e) {
      log.error("Cannot read file from resources.", e);
    }

    return entireMessage
            .replace(TITLE_PLACEHOLDER, title)
            .replace(BUTTON_LABEL_PLACEHOLDER, buttonLabel)
            .replace(BUTTON_LINK_PLACEHOLDER, buttonLink)
            .replace(BEGIN_CONTENT_PLACEHOLDER, beginContent)
            .replace(END_CONTENT_PLACEHOLDER, endContent);
  }

}
