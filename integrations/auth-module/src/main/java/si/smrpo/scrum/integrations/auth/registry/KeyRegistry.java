package si.smrpo.scrum.integrations.auth.registry;

import si.smrpo.scrum.integrations.auth.utils.KeyUtil;
import si.smrpo.scrum.persistence.auth.SigningKeyEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class KeyRegistry {
    
    private final AtomicReference<SigningKeyEntity> keyCache = new AtomicReference<>(null);
    
    public void setKey(SigningKeyEntity key) {
        this.keyCache.set(key);
    }
    
    public void clearRegistry() {
        this.keyCache.set(null);
    }
    
    public SigningKeyEntity getKey() {
        return this.keyCache.get();
    }
    
}
