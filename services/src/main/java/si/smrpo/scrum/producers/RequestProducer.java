package si.smrpo.scrum.producers;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.QueryStringDefaults;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@RequestScoped
public class RequestProducer {
    
    @Context
    private UriInfo uriInfo;
    
    @Context
    private HttpServletRequest httpRequest;
    
    @Produces
    @Dependent
    public QueryStringDefaults getQueryStringDefaults() {
        return new QueryStringDefaults()
            .maxLimit(100)
            .defaultLimit(25)
            .defaultOffset(0);
    }
    
    @Produces
    @RequestScoped
    public QueryParameters getQueryParams() {
        QueryStringDefaults qsd = getQueryStringDefaults();
        return qsd.builder().queryEncoded(uriInfo.getRequestUri().getRawQuery()).build();
    }
    
}
