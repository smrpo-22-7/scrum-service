package si.smrpo.scrum.integrations.auth.interceptors;


import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.services.SecurityService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.Priorities;

@SecureResource
@Interceptor
@Priority(Priorities.AUTHENTICATION)
public class AuthInterceptor {
    
    @Inject
    private SecurityService securityService;
    
    @AroundInvoke
    public Object authenticate(InvocationContext context) throws Exception {
        securityService.processSecurity(context);
        return context.proceed();
    }
    
}
