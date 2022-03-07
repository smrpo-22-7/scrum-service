package si.smrpo.scrum.api.mappers;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mjamsek.rest.exceptions.ForbiddenException;
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
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    
    private static final String FORBIDDEN_CODE = "error.forbidden";
    private static final String NOT_FOUND_CODE = "error.not-found";
    
    @Inject
    private Localizator localizator;
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(ForbiddenException exception) {
        String envName = ConfigurationUtil.getInstance().get("kumuluzee.env.name").orElse("prod");
        if (envName.equals("prod")) {
            return handleResponse(Response.Status.NOT_FOUND, NOT_FOUND_CODE);
        }
        return handleResponse(Response.Status.FORBIDDEN, FORBIDDEN_CODE);
    }
    
    private Response handleResponse(Response.Status status, String errorCode) {
        String message = localizator.getTranslation(errorCode, request.getLocale());
        
        ExceptionResponse error = new ExceptionResponse();
        error.setCode(errorCode);
        error.setMessage(message);
        error.setStatus(status.getStatusCode());
        
        return Response.status(status)
            .entity(error)
            .build();
    }
}
