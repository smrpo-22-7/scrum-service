package si.smrpo.scrum.integrations.messaging.email;

import si.smrpo.scrum.integrations.messaging.Message;

public class EmailMessage extends Message {

    private String subject;
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
