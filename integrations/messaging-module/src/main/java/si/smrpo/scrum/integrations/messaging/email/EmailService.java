package si.smrpo.scrum.integrations.messaging.email;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.services.Validator;
import si.smrpo.scrum.integrations.messaging.Message;
import si.smrpo.scrum.integrations.messaging.MessagingException;
import si.smrpo.scrum.integrations.messaging.MessagingService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@ApplicationScoped
public class EmailService implements MessagingService {
    
    private static final Logger LOG = LogManager.getLogger(EmailService.class.getName());
    
    @Inject
    private EmailConfig emailConfig;
    
    @Inject
    private Validator validator;
    
    private Properties emailProperties;
    
    @PostConstruct
    private void init() {
        this.emailProperties = new Properties();
        this.emailProperties.put("mail.smtp.host", emailConfig.getHost());
        this.emailProperties.put("mail.smtp.user", emailConfig.getUsername());
        this.emailProperties.put("mail.smtp.password", emailConfig.getPassword());
        this.emailProperties.put("mail.smtp.port", emailConfig.getPort());
        this.emailProperties.put("mail.smtp.auth", "true");
        this.emailProperties.put("mail.smtp.ssl.enable", "true");
    }
    
    private void validateMessage(Message message) {
        validator.assertNotBlank(message.getRecipient());
        validator.assertNotBlank(message.getContent());
        if (message instanceof EmailMessage) {
            EmailMessage emailMessage = (EmailMessage) message;
            validator.assertNotBlank(emailMessage.getSubject());
        } else {
            throw new IllegalArgumentException("Invalid message! Message should be of type EmailMessage.");
        }
    }
    
    @Override
    public void sendMessage(Message message) throws MessagingException {
        validateMessage(message);
        try {
            LOG.debug("Preparing to send e-mail message...");
            Session session = Session.getInstance(emailProperties);
            javax.mail.Message emailMessage = createEmailMessage(message, session);
    
            try (Transport transport = session.getTransport("smtp")) {
                transport.connect(emailConfig.getHost(), emailConfig.getUsername(), emailConfig.getPassword());
                transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
            } catch (javax.mail.MessagingException e) {
                LOG.error("Error opening transport for sending email!", e);
                throw new EmailException("messaging.email.transport.error", e);
            }
            LOG.debug("Sent e-mail message!");
        } catch (javax.mail.MessagingException e) {
            LOG.error("Error sending email!", e);
            throw new EmailException("messaging.email.error", e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error encoding message's content!", e);
            throw new EmailException("messaging.email.encoding.error", e);
        }
    }
    
    private MimeMessage createEmailMessage(Message message, Session session) throws javax.mail.MessagingException, UnsupportedEncodingException {
        EmailMessage emailMessage = (EmailMessage) message;
        
        MimeMessage email = new MimeMessage(session);
        InternetAddress recipient = new InternetAddress(emailMessage.getRecipient());
        InternetAddress sender = new InternetAddress(emailConfig.getUsername(), emailConfig.getDisplayName());
        
        email.setFrom(sender);
        email.addRecipient(javax.mail.Message.RecipientType.TO, recipient);
    
        Multipart content = new MimeMultipart();
        
        BodyPart htmlContent = new MimeBodyPart();
        htmlContent.setContent(emailMessage.getContent(), "text/html; charset=utf-8");
        
        content.addBodyPart(htmlContent);
        
        email.setSubject(emailMessage.getSubject());
        email.setContent(content);
        
        return email;
    }
}
