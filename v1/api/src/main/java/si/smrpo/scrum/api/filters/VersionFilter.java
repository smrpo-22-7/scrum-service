package si.smrpo.scrum.api.filters;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mjamsek.rest.Rest;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class VersionFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        if (servletResponse instanceof HttpServletResponse) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
        
            ConfigurationUtil configUtil = ConfigurationUtil.getInstance();
        
            response.setHeader(Rest.HttpHeaders.X_SERVICE_ENV, configUtil.get("kumuluzee.env.name").orElse(""));
            response.setHeader(Rest.HttpHeaders.X_SERVICE_NAME, configUtil.get("kumuluzee.name").orElse(""));
            response.setHeader(Rest.HttpHeaders.X_SERVICE_VERSION, configUtil.get("kumuluzee.version").orElse(""));
        
            chain.doFilter(servletRequest, response);
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }
    
    @Override
    public void destroy() {
    
    }
}
