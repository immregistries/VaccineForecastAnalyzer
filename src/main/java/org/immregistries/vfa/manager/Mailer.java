package org.immregistries.vfa.manager;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.immregistries.vfa.CentralControl;
import org.immregistries.vfa.model.SystemProperty;

public class Mailer {
  /**
   * Send a single email.
   */

  public void sendEmail(String toEmail, String subject, String messageBody) {
    fetchConfig();
    final String fromEmail = mailProps.getProperty("mail.from");
    sendEmail(fromEmail, toEmail, subject, messageBody);
  }

  public void sendEmail(String fromEmail, String toEmail, String subject, String messageBody) {
    // Here, no Authenticator argument is used (it is null).
    // Authenticators are used to prompt the user for user
    // name and password.

    final String username = mailProps.getProperty("mail.smtp.user");
    final String password = mailProps.getProperty("mail.smtp.password");
    Session session = Session.getDefaultInstance(mailProps, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });
    MimeMessage message = new MimeMessage(session);
    try {
      // the "from" address may be set in code, or set in the
      // config file under "mail.from" ; here, the latter style is used
      // message.setFrom( new InternetAddress(aFromEmailAddr) );
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
      if (mailProps.get("mail.from") != null)
      {
        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(mailProps.getProperty("mail.from")));
      }
      message.setFrom(new InternetAddress(fromEmail));
      message.setSubject(subject);
      message.setText(messageBody);
      message.setSentDate(new Date());

      String auth = mailProps.getProperty("mail.smtp.auth");
      if (auth != null && auth.equals("true")) {
        final String host = mailProps.getProperty("mail.smtp.host");
        final int port = Integer.parseInt(mailProps.getProperty("mail.smtp.port"));

        Transport transport = session.getTransport("smtps");
        transport.connect(host, port, username, password);
        transport.sendMessage(message, message.getAllRecipients());
      } else {
        Transport.send(message);
      }
    } catch (MessagingException ex) {
      System.err.println("Cannot send email. " + ex);
    }
  }

  /**
   * Allows the config to be refreshed at runtime, instead of requiring a
   * restart.
   */
  public static void refreshConfig() {
    mailProps = null;
    fetchConfig();
  }

  // PRIVATE //

  private static Properties mailProps = null;

  /**
   * Open a specific text file containing mail server parameters, and populate a
   * corresponding Properties object.
   */
  private static synchronized void fetchConfig() {
    if (mailProps == null) {
      mailProps = new Properties();
      SessionFactory factory = CentralControl.getSessionFactory();
      org.hibernate.Session dataSession = factory.openSession();

      Query query = dataSession.createQuery("from SystemProperty");
      List<SystemProperty> systemPropertyList = query.list();
      for (SystemProperty systemProperty : systemPropertyList) {
        if (systemProperty.getPropertyName().startsWith("mail.")) {
          mailProps.put(systemProperty.getPropertyName(), systemProperty.getPropertyValue());
        }
      }
      dataSession.close();
    }
  }
}
