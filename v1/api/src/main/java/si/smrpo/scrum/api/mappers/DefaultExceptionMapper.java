package si.smrpo.scrum.api.mappers;

import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.services.Localizator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@RequestScoped
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<RestException> {
    
    @Inject
    private Localizator localizator;
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(RestException exception) {
        if (exception.getStatus() == 400 || exception.getStatus() >= 500) {
            exception.printStackTrace();
        }
        return MapperUtil.mapException(exception, localizator, request.getLocale());
    }
}
