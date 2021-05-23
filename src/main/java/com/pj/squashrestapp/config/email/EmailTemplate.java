package com.pj.squashrestapp.config.email;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

/** */
@Slf4j
@Builder
public class EmailTemplate {

  private static final String TEMPLATE_WITH_BUTTON_PATH =
      "templates" + File.separator + "email_template_with_button.html";
  private static final String TEMPLATE_PLAIN_PATH =
      "templates" + File.separator + "email_template_plain.html";

  private static final String CSS_MEDIA_PATH =
      "templates" + File.separator + "email_style_media.css";
  private static final String CSS_BODY_1_PATH =
      "templates" + File.separator + "email_style_body_1.css";
  private static final String CSS_BODY_2_PATH =
      "templates" + File.separator + "email_style_body_2.css";

  private static final String CSS_MEDIA_PLACEHOLDER = "__MEDIA_STYLE_CSS__";
  private static final String CSS_BODY_1_PLACEHOLDER = "__BODY1_STYLE_CSS__";
  private static final String CSS_BODY_2_PLACEHOLDER = "__BODY2_STYLE_CSS__";

  private static final String TITLE_PLACEHOLDER = "_Title_";
  private static final String USERNAME_PLACEHOLDER = "_Username_";
  private static final String BUTTON_LABEL_PLACEHOLDER = "_Button_label_";
  private static final String BUTTON_LINK_PLACEHOLDER = "_Button_link_";
  private static final String BEGIN_CONTENT_PLACEHOLDER = "_Begin_";
  private static final String END_CONTENT_PLACEHOLDER = "_End_";

  private final boolean isWithButton;
  private final String username;
  private final String title;
  private final String buttonLabel;
  private final String buttonLink;
  private final String beginContent;
  private final String endContent;

  public final String createHtmlContent() {
    String entireMessage =
        isWithButton
            ? getContentFromFile(TEMPLATE_WITH_BUTTON_PATH)
            : getContentFromFile(TEMPLATE_PLAIN_PATH);

    final String cssMediaContent = getContentFromFile(CSS_MEDIA_PATH);
    final String cssBody1Content = getContentFromFile(CSS_BODY_1_PATH);
    final String cssBody2Content = getContentFromFile(CSS_BODY_2_PATH);

    entireMessage =
        entireMessage
            .replace(CSS_MEDIA_PLACEHOLDER, cssMediaContent)
            .replace(CSS_BODY_1_PLACEHOLDER, cssBody1Content)
            .replace(CSS_BODY_2_PLACEHOLDER, cssBody2Content)
            .replace(TITLE_PLACEHOLDER, title)
            .replace(BEGIN_CONTENT_PLACEHOLDER, "Hi " + username + ", <br>" + beginContent)
            .replace(END_CONTENT_PLACEHOLDER, endContent);

    if (isWithButton) {
      entireMessage =
          entireMessage
              .replace(BUTTON_LABEL_PLACEHOLDER, buttonLabel)
              .replace(BUTTON_LINK_PLACEHOLDER, buttonLink);
    }

    return entireMessage;
  }

  private String getContentFromFile(final String filepath) {
    final ClassPathResource classPathResource = new ClassPathResource(filepath);

    String entireMessage = "";
    try (final InputStream inputStream = classPathResource.getInputStream()) {
      entireMessage = new String(inputStream.readAllBytes());
    } catch (final IOException e) {
      log.error("Cannot read file from resources.", e);
    }
    return entireMessage;
  }
}
