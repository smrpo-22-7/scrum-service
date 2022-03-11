package si.smrpo.scrum.integrations.auth;

import com.kumuluz.ee.common.Extension;
import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.*;
import com.kumuluz.ee.common.utils.ResourceUtils;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.smrpo.scrum.integrations.auth.filters.AuthorizationRequestFilter;
import si.smrpo.scrum.integrations.auth.servlets.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@EeExtensionDef(name = "ScrumAuth", group = EeExtensionGroup.SECURITY)
@EeComponentDependencies({
    @EeComponentDependency(EeComponentType.SERVLET),
    @EeComponentDependency(EeComponentType.CDI),
    @EeComponentDependency(EeComponentType.JAX_RS)
})
public class AuthModuleExtension implements Extension {
    
    private static final Logger LOG = LogManager.getLogger(AuthModuleExtension.class.getName());
    
    @Override
    public void load() {
    
    }
    
    @Override
    public void init(KumuluzServerWrapper kumuluzServerWrapper, EeConfig eeConfig) {
        if (kumuluzServerWrapper.getServer() instanceof ServletServer) {
            LOG.info("Initializing auth module...");
            ServletServer servletServer = (ServletServer) kumuluzServerWrapper.getServer();
    
            servletServer.registerServlet(AuthorizationServlet.class, "/protocol/oidc/auth");
            servletServer.registerServlet(TokenServlet.class, "/protocol/oidc/token");
            servletServer.registerServlet(EndSessionServlet.class, "/protocol/oidc/logout");
            servletServer.registerServlet(LoginServlet.class, "/login");
            servletServer.registerServlet(ErrorServlet.class, "/error");
    
            URL staticFolder = ResourceUtils.class.getClassLoader().getResource("webapp/static");
            if (staticFolder != null ) {
                Map<String, String> staticResourceParams = new HashMap<>();
                staticResourceParams.put("resourceBase", staticFolder.toString());
                servletServer.registerServlet(StaticFilesServlet.class, "/static/*", staticResourceParams);
            } else {
                LOG.warn("Unable to find static files folder!");
            }
    
            servletServer.registerFilter(AuthorizationRequestFilter.class, "/protocol/oidc/auth");
            LOG.info("Auth module Initialized!");
        }
    }
}
