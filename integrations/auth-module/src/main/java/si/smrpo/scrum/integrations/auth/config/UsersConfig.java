package si.smrpo.scrum.integrations.auth.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("users")
public class UsersConfig {
    
    @ConfigValue("password.min-length")
    private int minPasswordLength;
    
    @ConfigValue("password.max-length")
    private int maxPasswordLength;
    
    public int getMinPasswordLength() {
        return minPasswordLength;
    }
    
    public void setMinPasswordLength(int minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
    }
    
    public int getMaxPasswordLength() {
        return maxPasswordLength;
    }
    
    public void setMaxPasswordLength(int maxPasswordLength) {
        this.maxPasswordLength = maxPasswordLength;
    }
}
