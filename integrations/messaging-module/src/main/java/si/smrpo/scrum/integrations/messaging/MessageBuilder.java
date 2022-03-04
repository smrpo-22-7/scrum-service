package si.smrpo.scrum.integrations.messaging;

import si.smrpo.scrum.integrations.messaging.email.EmailMessageBuilder;
import si.smrpo.scrum.integrations.messaging.sms.SmsMessageBuilder;

public class MessageBuilder {
    
    public static EmailMessageBuilder newEmailMessage() {
        return EmailMessageBuilder.newBuilder();
    }
    
    public static SmsMessageBuilder newSmsMessage() {
        throw new IllegalStateException();
    }
}
