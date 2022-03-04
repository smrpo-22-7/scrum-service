package si.smrpo.scrum.integrations.messaging;

public abstract class Message {
    
    protected String recipient;
    
    protected String content;
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
