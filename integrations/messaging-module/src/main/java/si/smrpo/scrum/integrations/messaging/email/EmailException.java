package si.smrpo.scrum.integrations.messaging.email;

import si.smrpo.scrum.integrations.messaging.MessagingException;

public class EmailException extends MessagingException {
    
    private static final String SERVICE_NAME = "ZohoEmailClient";
    
    public EmailException(String code) {
        super(code, SERVICE_NAME);
    }
    
    public EmailException(String code, Throwable cause) {
        super(code, SERVICE_NAME, cause);
    }
}
