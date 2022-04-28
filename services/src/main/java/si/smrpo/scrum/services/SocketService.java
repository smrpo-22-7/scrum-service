package si.smrpo.scrum.services;

import si.smrpo.scrum.lib.ws.SocketMessage;

import javax.websocket.Session;
import java.util.Set;

public interface SocketService {
    
    void broadcast(SocketMessage message);
    
    void broadcast(SocketMessage message, Set<Session> sessions);
    
    void sendMessage(SocketMessage message, Session session);
    
    void sendMessage(SocketMessage message, String userId);
    
}
