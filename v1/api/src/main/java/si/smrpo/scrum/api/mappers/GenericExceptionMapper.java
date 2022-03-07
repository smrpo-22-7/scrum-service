package si.smrpo.scrum.api.mappers;

import com.mjamsek.rest.exceptions.dto.ExceptionResponse;
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
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    
    private static final String ERROR_CODE = "error.server";
    
    @Inject
    private Localizator localizator;
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();
        
        String message = localizator.getTranslation(ERROR_CODE, request.getLocale());
        
        ExceptionResponse error = new ExceptionResponse();
        error.setCode(ERROR_CODE);
        error.setMessage(message);
        error.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        
        return Response.serverError().entity(error).build();
    }
}
