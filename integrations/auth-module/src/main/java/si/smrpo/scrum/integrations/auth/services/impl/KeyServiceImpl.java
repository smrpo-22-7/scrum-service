package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import si.smrpo.scrum.integrations.auth.registry.KeyRegistry;
import si.smrpo.scrum.integrations.auth.services.KeyService;
import si.smrpo.scrum.integrations.auth.utils.KeyUtil;
import si.smrpo.scrum.persistence.auth.SigningKeyEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.security.KeyPair;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class KeyServiceImpl implements KeyService {
    
    private static final Logger LOG = LogManager.getLogger(KeyServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private KeyRegistry keyRegistry;
    
    @Override
    public void initializeKeys() {
        LOG.info("Initializing signing keys...");
        TypedQuery<Long> query = em.createNamedQuery(SigningKeyEntity.CHECK_KEY_EXISTS, Long.class);
        
        try {
            long existingKeys = query.getSingleResult();
            if (existingKeys == 0) {
                persistNewKey(true);
                LOG.info("Signing keys initialized!");
            }
        } catch (PersistenceException e) {
            LOG.error("Error initializing signing key!", e);
        }
    }
    
    @Override
    public SigningKeyEntity getSigningKey() {
        List<SigningKeyEntity> keys = JPAUtils.getEntityStream(em, SigningKeyEntity.class, new QueryParameters())
            .limit(1)
            .collect(Collectors.toList());
        
        if (keys.size() > 0) {
            return keys.get(0);
        }
        throw new IllegalStateException("No signing keys!");
    }
    
    @Override
    public void rotateKeys() {
        LOG.info("Rotating signing keys...");
        Query query = em.createNamedQuery(SigningKeyEntity.REMOVE_KEYS);
        try {
            em.getTransaction().begin();
            query.executeUpdate();
            SigningKeyEntity newKey = persistNewKey(false);
            em.getTransaction().commit();
            
            keyRegistry.clearRegistry();
            keyRegistry.setKey(newKey);
            
            LOG.info("Signing keys rotated");
        } catch (PersistenceException e) {
            LOG.error("Error rotating keys!", e);
            em.getTransaction().rollback();
        }
    }
    
    @Override
    public void loadKeysToRegistry() {
        SigningKeyEntity key = getSigningKey();
        this.keyRegistry.setKey(key);
        LOG.info("Signing keys loaded into key registry!");
    }
    
    private SigningKeyEntity persistNewKey(boolean useTransaction) {
        try {
            KeyPair keyPair = generateRsaKeyPair();
            
            SigningKeyEntity key = new SigningKeyEntity();
            key.setAlgorithm("RS256");
            key.setPrivateKey(KeyUtil.keyToString(keyPair.getPrivate()));
            key.setPublicKey(KeyUtil.keyToString(keyPair.getPublic()));
            
            if (useTransaction) {
                em.getTransaction().begin();
            }
            em.persist(key);
            if (useTransaction) {
                em.getTransaction().commit();
            }
            return key;
        } catch (PersistenceException e) {
            LOG.error("Error initializing signing key!", e);
            if (useTransaction) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    private KeyPair generateRsaKeyPair() {
        try {
            RSAKey rsaKey = new RSAKeyGenerator(2048)
                .keyUse(KeyUse.SIGNATURE)
                .generate();
            return rsaKey.toKeyPair();
        } catch (JOSEException e) {
            LOG.error("Invalid key specifications! Unable to generate RSA keypair!", e);
            throw new IllegalArgumentException("Invalid key specifications!");
        }
    }
}
