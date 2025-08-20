package co.simplon.jamixbusiness.commons;

import co.simplon.jamixbusiness.config.JamixConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

  private final JavaMailSender mailSender;
  private final JamixConfig jamixConfig;

  public EmailSender(final JavaMailSender mailSender, final JamixConfig jamixConfig) {
    this.mailSender = mailSender;
    this.jamixConfig = jamixConfig;
  }

  public void sendEmail(String to, String subject, String htmlText) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setFrom(jamixConfig.emailConfig().from());
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(htmlText, true);

    mailSender.send(message);
  }
}
