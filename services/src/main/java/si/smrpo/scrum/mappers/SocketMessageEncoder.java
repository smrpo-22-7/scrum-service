package si.smrpo.scrum.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import si.smrpo.scrum.lib.ws.SocketMessage;
import si.smrpo.scrum.producers.JacksonProducer;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class SocketMessageEncoder implements Encoder.Text<SocketMessage> {
    
    private static final ObjectMapper objectMapper = JacksonProducer.getNewMapper();
    
    @Override
    public String encode(SocketMessage object) throws EncodeException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void init(EndpointConfig config) {
    
    }
    
    @Override
    public void destroy() {
    
    }
}
