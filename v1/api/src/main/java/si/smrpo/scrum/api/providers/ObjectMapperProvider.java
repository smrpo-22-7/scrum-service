package si.smrpo.scrum.api.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import si.smrpo.scrum.producers.JacksonProducer;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
    
    @Override
    public ObjectMapper getContext(Class<?> aClass) {
        return JacksonProducer.getNewMapper();
    }
}
