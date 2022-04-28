package si.smrpo.scrum.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import si.smrpo.scrum.lib.ws.SocketMessage;
import si.smrpo.scrum.producers.JacksonProducer;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;

public class SocketMessageDecoder implements Decoder.Text<SocketMessage> {
    
    private static final ObjectMapper objectMapper = JacksonProducer.getNewMapper();
    
    @Override
    public SocketMessage decode(String s) throws DecodeException {
        try {
            return objectMapper.readValue(s, SocketMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean willDecode(String s) {
        return true;
    }
    
    @Override
    public void init(EndpointConfig config) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
