package si.smrpo.scrum.api.websocket;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import si.smrpo.scrum.context.SocketSessionContext;
import si.smrpo.scrum.integrations.auth.services.SigningService;
import si.smrpo.scrum.lib.enums.SocketMessageType;
import si.smrpo.scrum.lib.ws.SocketMessage;
import si.smrpo.scrum.services.SocketService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class WebsocketHandler {
    
    public static final String USER_ID_PROPERTY = "userId";
    
    private static final Logger LOG = LogManager.getLogger(WebsocketHandler.class.getName());
    
    @Inject
    private SocketService socketService;
    
    @Inject
    private SigningService signingService;
    
    @ActivateRequestContext
    public void handleMessage(SocketMessage message, Session session) {
        if (message.getType().equals(SocketMessageType.PING.name())) {
            handlePingRequest(session);
            return;
        }
        
        Optional<JWTClaimsSet> credentials = validateCredentials(message.getAccessToken());
        if (credentials.isEmpty()) {
            return;
        }
        
        if (message.getType().equals(SocketMessageType.REGISTER.name())) {
            handleRegistration(credentials.get(), session);
            return;
        }
        
        // Handle incoming messages
    }
    
    public void openSession(Session session) {
        session.setMaxIdleTimeout(0);
        LOG.info("Registered new socket session with id: " + session.getId());
        SocketSessionContext.openSession(session);
    }
    
    public void closeSession(Session session) {
        SocketSessionContext.closeSession(session);
    }
    
    public void handleError(Throwable throwable, Session session) {
        LOG.error("Session id: {}, error: {}", session.getId(), throwable.getMessage());
        throwable.printStackTrace();
        
        if (throwable.getCause() instanceof TimeoutException) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.SERVICE_RESTART, "Timeout"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handlePingRequest(Session session) {
        SocketMessage pong = new SocketMessage();
        pong.setType(SocketMessageType.PONG.name());
        socketService.sendMessage(pong, session);
    }
    
    private void handleRegistration(JWTClaimsSet claimsSet, Session session) {
        LOG.info("Received REGISTRATION from session with id: " + session.getId());
        String userId = claimsSet.getSubject();
        // Close all sessions with given userId
        SocketSessionContext.getAllSessions().stream()
            .filter(s -> {
                if (s == session) {
                    return false;
                }
                if (s.getUserProperties().containsKey(USER_ID_PROPERTY)) {
                    return s.getUserProperties().get(USER_ID_PROPERTY).equals(userId);
                }
                return false;
            })
            .forEach(s -> {
                try {
                    if (!session.getId().equals(s.getId())) {
                        s.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Only one active connection per device allowed!"));
                    }
                } catch (IOException ignored) {
                
                }
            });
        
        // Set userId for current session.
        session.getUserProperties().put(USER_ID_PROPERTY, userId);
    }
    
    private Optional<JWTClaimsSet> validateCredentials(String accessToken) {
        if (accessToken == null) {
            return Optional.empty();
        }
        
        try {
            SignedJWT jwt = signingService.validateJwt(accessToken);
            return Optional.of(jwt.getJWTClaimsSet());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
