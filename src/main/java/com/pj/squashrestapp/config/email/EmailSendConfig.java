package com.pj.squashrestapp.config.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
public class EmailSendConfig {

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

  public void sendEmail(final String receiver, final String subject, final Object content) {
    final Properties properties = buildProperties();
    final Session session = buildSession(properties);

    try {
      final Message message = prepareMessage(session, receiver, subject, content);
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

  private Message prepareMessage(
      final Session session, final String receiver, final String subject, final Object content)
      throws MessagingException, UnsupportedEncodingException {
    final Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(senderEmailAdress, senderName, "UTF8"));

    message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));

    message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(senderEmailAdress));

    message.setSubject(subject);
    message.setContent(content, "text/html; charset=UTF-8");
    return message;
  }
}
