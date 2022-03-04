package si.smrpo.scrum.integrations.messaging.email;

import si.smrpo.scrum.integrations.messaging.Message;

public class EmailMessageBuilder {
    
    private final EmailMessage message;
    
    public static EmailMessageBuilder newBuilder() {
        return new EmailMessageBuilder();
    }
    
    private EmailMessageBuilder() {
        this.message = new EmailMessage();
    }
    
    public EmailMessageBuilder recipient(String recipient) {
        this.message.setRecipient(recipient);
        return this;
    }
    
    public EmailMessageBuilder subject(String subject) {
        this.message.setSubject(subject);
        return this;
    }
    
    public EmailMessageBuilder content(String content) {
        this.message.setContent(content);
        return this;
    }
    
    public Message build() {
        return this.message;
    }
    
}
