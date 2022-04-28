package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.smrpo.scrum.context.SocketSessionContext;
import si.smrpo.scrum.lib.ws.SocketMessage;
import si.smrpo.scrum.services.SocketService;

import javax.enterprise.context.RequestScoped;
import javax.websocket.Session;
import java.util.Set;
import java.util.stream.Collectors;

@RequestScoped
public class SocketServiceImpl implements SocketService {
    
    private static final Logger LOG = LogManager.getLogger(SocketServiceImpl.class.getName());
    
    public static final String USER_ID_PROPERTY = "userId";
    
    @Override
    public void broadcast(SocketMessage message) {
        broadcast(message, SocketSessionContext.getAllSessions());
    }
    
    @Override
    public void broadcast(SocketMessage message, Set<Session> sessions) {
        sessions.forEach(session -> {
            session.getAsyncRemote().sendObject(message);
        });
    }
    
    @Override
    public void sendMessage(SocketMessage message, Session session) {
        session.getAsyncRemote().sendObject(message);
    }
    
    @Override
    public void sendMessage(SocketMessage message, String userId) {
        var sessions = SocketSessionContext.getAllSessions().stream()
            .filter(session -> {
                var storedId = session.getUserProperties().get(USER_ID_PROPERTY);
                return storedId != null && storedId.equals(userId);
            })
            .collect(Collectors.toSet());
        
        if (sessions.size() > 0) {
            sessions.forEach(session -> {
                sendMessage(message, session);
            });
        } else {
            LOG.warn("No active session for user!");
        }
    }
}
