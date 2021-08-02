package com.pj.squashrestapp.hexagonal.email;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** */
@Slf4j
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("email")
class EmailSendConfig {

  @Value(value = "${sender_email_adress:}")
  private String senderEmailAdress;

  @Value(value = "${sender_name:}")
  private String senderName;

  @Value(value = "${password:}")
  private String password;

  @Value(value = "${smtp_host:}")
  private String smtpHost;

  @Value(value = "${smtp_port:}")
  private String smtpPort;

  void sendEmailWithAttachment(
      final String receiver, final String subject, final Object content, final File... files) {
    final Properties properties = buildProperties();
    final Session session = buildSession(properties);

    try {
      final Message message =
          prepareMessageWithAttachments(session, receiver, subject, content, files);
      Transport.send(message);
      log.info("[{}] email to [{}] has been sent succesfully", subject, receiver);

    } catch (final MessagingException | UnsupportedEncodingException e) {
      log.error("[{}] email to [{}] has not been sent!", subject, receiver);
      log.error("Exception", e);
    }
  }

  void sendEmailWithHtmlContent(
      final String receiver, final String subject, final String htmlMessageContent) {
    final Properties properties = buildProperties();
    final Session session = buildSession(properties);

    try {
      final Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(senderEmailAdress, senderName, "UTF8"));
      message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
      message.setSubject(subject);
      message.setContent(htmlMessageContent, "text/html; charset=UTF-8");
      Transport.send(message);
      log.info("[{}] email to [{}] has been sent succesfully", subject, receiver);

    } catch (final MessagingException | UnsupportedEncodingException e) {
      log.error("[{}] email to [{}] has not been sent!", subject, receiver);
      log.error("Exception", e);
    }
  }

  private Properties buildProperties() {
    final Properties prop = new Properties();
    prop.put("mail.smtp.host", smtpHost);
    prop.put("mail.smtp.port", smtpPort);
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.starttls.enable", "true");
    return prop;
  }

  private Session buildSession(final Properties properties) {
    return Session.getInstance(
        properties,
        new Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmailAdress, password);
          }
        });
  }

  private Message prepareMessageWithAttachments(
      final Session session,
      final String receiver,
      final String subject,
      final Object content,
      final File... files)
      throws MessagingException, UnsupportedEncodingException {
    final Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(senderEmailAdress, senderName, "UTF8"));
    message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
    message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(senderEmailAdress));
    message.setSubject(subject);

    final BodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent(content, "text/html; charset=UTF-8");

    final MimeBodyPart attachmentPart = new MimeBodyPart();
    for (final File file : files) {
      try {
        attachmentPart.attachFile(file);
      } catch (final IOException e) {
        log.error("Cannot attach file {}", file);
      }
    }

    final Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBodyPart);
    multipart.addBodyPart(attachmentPart);

    message.setContent(multipart);
    return message;
  }
}
