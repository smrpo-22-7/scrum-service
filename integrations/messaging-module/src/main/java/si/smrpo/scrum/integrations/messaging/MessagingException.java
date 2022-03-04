package si.smrpo.scrum.integrations.messaging;

import com.mjamsek.rest.exceptions.ServiceCallException;

public class MessagingException extends ServiceCallException {
    
    public MessagingException(String code, String service) {
        super(code, service);
    }
    
    public MessagingException(String code, String service, Throwable cause) {
        super(code, service, cause);
    }
}
