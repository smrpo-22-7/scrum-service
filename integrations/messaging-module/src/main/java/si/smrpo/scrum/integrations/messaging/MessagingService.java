package si.smrpo.scrum.integrations.messaging;

public interface MessagingService {

    void sendMessage(Message message) throws MessagingException;

}
