package si.smrpo.scrum.api.mappers;

import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.services.Localizator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@RequestScoped
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {
    
    @Inject
    private Localizator localizator;
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(UnauthorizedException exception) {
        return MapperUtil.mapException(exception, localizator, request.getLocale());
    }
}
