package si.smrpo.scrum.api.mappers;

import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.dto.ExceptionResponse;
import com.mjamsek.rest.services.Localizator;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Locale;

public class MapperUtil {
    
    private MapperUtil() {
    
    }
    
    public static Response mapException(RestException e, Localizator localizator, Locale locale) {
        if (e.getResponse().getParams() != null && e.getResponse().getParams().length > 0) {
            String message = localizator.getTranslation(
                e.getResponse().getCode(),
                locale,
                e.getResponse().getParams()
            );
            e.setMessage(message);
        } else {
            String message = localizator.getTranslation(
                e.getResponse().getCode(),
                locale
            );
            e.setMessage(message);
        }
        return e.getResponse().createResponse();
    }
    
    public static Response mapException(WebApplicationException e, Localizator localizator, Locale locale) {
        Response resp = e.getResponse();
        ExceptionResponse body = new ExceptionResponse();
        body.setStatus(resp.getStatus());
        body.setMessage(e.getMessage());
        return Response.status(resp.getStatus())
            .entity(body)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
    
}
