package si.smrpo.scrum.api.mappers;

import com.mjamsek.rest.exceptions.ValidationException;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@RequestScoped
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    
    @Override
    public Response toResponse(ValidationException exception) {
        return exception.getResponse().createResponse();
    }
}
