package si.smrpo.scrum.api.websocket;

import si.smrpo.scrum.lib.ws.SocketMessage;
import si.smrpo.scrum.mappers.SocketMessageDecoder;
import si.smrpo.scrum.mappers.SocketMessageEncoder;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/ws/sync", encoders = SocketMessageEncoder.class, decoders = SocketMessageDecoder.class)
public class WebsocketEndpoint {
    
    @Inject
    private WebsocketHandler handler;
    
    @OnMessage
    public void onMessage(SocketMessage message, Session session) {
        if (message != null) {
            handler.handleMessage(message, session);
        }
    }
    
    @OnOpen
    public void onOpen(Session session) {
        handler.openSession(session);
    }
    
    @OnClose
    public void onClose(Session session) {
        handler.closeSession(session);
    }
    
    @OnError
    public void onError(Throwable throwable, Session session) {
        handler.handleError(throwable, session);
    }
}
