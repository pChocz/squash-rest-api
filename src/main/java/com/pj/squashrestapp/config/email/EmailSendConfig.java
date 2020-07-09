package com.pj.squashrestapp.config.email;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 */
@Slf4j
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("email")
public class EmailSendConfig {

  @Value(value = "${sender_email_adress:}")
  private String senderEmailAdress;

  @Value(value = "${password:}")
  private String password;

  @Value(value = "${smtp_host:}")
  private String smtpHost;

  @Value(value = "${smtp_port:}")
  private String smtpPort;


  public void sendEmail(final String receiver, final String subject, final String content) {
    final Properties properties = buildProperties();
    final Session session = buildSession(properties);

    try {
      final Message message = prepareMessage(session, receiver, subject, content);
      Transport.send(message);
      log.info("Email sent succesfully");

    } catch (final MessagingException e) {
      log.error("Email not sent!", e);
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
    return Session.getInstance(properties,
            new Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmailAdress, password);
              }
            }
    );
  }

  private Message prepareMessage(final Session session, final String receiver,
                                 final String subject, final String content) throws MessagingException {
    final Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(senderEmailAdress));
    message.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(receiver)
    );
    message.setSubject(subject);
    message.setText(content);
    return message;
  }

}
