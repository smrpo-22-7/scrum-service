package si.smrpo.scrum.integrations.preferences.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.integrations.preferences.UserPreferences;
import si.smrpo.scrum.lib.enums.DataType;
import si.smrpo.scrum.persistence.identifiers.UserPreferenceId;
import si.smrpo.scrum.integrations.preferences.UserPreferenceKey;
import si.smrpo.scrum.persistence.users.UserPreferencesEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@RequestScoped
public class UserPreferencesImpl implements UserPreferences {

    public static final Logger LOG = LogManager.getLogger(UserPreferencesImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    private ObjectMapper objectMapper;
    
    @PostConstruct
    private void init() {
        this.objectMapper = new ObjectMapper();
    }
    
    
    @Override
    public Map<UserPreferenceKey, UserPreferencesEntity> getUserPreferences(String userId) {
        TypedQuery<UserPreferencesEntity> query = em.createNamedQuery(UserPreferencesEntity.GET_BY_USER, UserPreferencesEntity.class);
        query.setParameter("userId", userId);
        
        return query
            .getResultStream()
            .collect(toMap(pref -> UserPreferenceKey.parse(pref.getPreferenceKey()), p -> p));
    }
    
    @Override
    public Optional<UserPreferencesEntity> getUserPreference(UserPreferenceKey key, String userId) {
        TypedQuery<UserPreferencesEntity> query = em.createNamedQuery(UserPreferencesEntity.GET_BY_KEY_AND_USER, UserPreferencesEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("key", key.key());
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<String> getString(UserPreferenceKey key, String userId) {
        return getUserPreference(key, userId)
            .filter(pref -> pref.getDataType().equals(DataType.STRING))
            .map(UserPreferencesEntity::getPreferenceValue);
    }
    
    @Override
    public Optional<Integer> getLong(UserPreferenceKey key, String userId) {
        return getUserPreference(key, userId)
            .filter(pref -> pref.getDataType().equals(DataType.STRING))
            .map(UserPreferencesEntity::getPreferenceValue)
            .map(Integer::parseInt);
    }
    
    @Override
    public Optional<Boolean> getBoolean(UserPreferenceKey key, String userId) {
        return getUserPreference(key, userId)
            .filter(pref -> pref.getDataType().equals(DataType.STRING))
            .map(UserPreferencesEntity::getPreferenceValue)
            .map(Boolean::parseBoolean);
    }
    
    @Override
    public Optional<Double> getDecimal(UserPreferenceKey key, String userId) {
        return getUserPreference(key, userId)
            .filter(pref -> pref.getDataType().equals(DataType.STRING))
            .map(UserPreferencesEntity::getPreferenceValue)
            .map(Double::parseDouble);
    }
    
    @Override
    public Optional<JsonNode> getJSON(UserPreferenceKey key, String userId) {
        return getUserPreference(key, userId)
            .filter(pref -> pref.getDataType().equals(DataType.STRING))
            .map(UserPreferencesEntity::getPreferenceValue)
            .map(value -> {
                try {
                    return objectMapper.readTree(value);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("Not a valid JSON string!");
                }
            });
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, String value, String userId) {
        persistUserPreference(key, value, DataType.STRING, userId);
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, Long value, String userId) {
        persistUserPreference(key, value.toString(), DataType.INTEGER, userId);
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, Boolean value, String userId) {
        persistUserPreference(key, value.toString(), DataType.BOOLEAN, userId);
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, Double value, String userId) {
        persistUserPreference(key, value.toString(), DataType.FLOAT, userId);
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, ObjectNode value, String userId) {
        try {
            String stringifiedJson = objectMapper.writeValueAsString(value);
            persistUserPreference(key, stringifiedJson, DataType.JSON, userId);
        } catch (JsonProcessingException e) {
            LOG.error(e);
            throw new IllegalArgumentException("Unable to serialize JSON value!");
        }
    }
    
    private void persistUserPreference(UserPreferenceKey key, String value, DataType dataType, String userId) {
        Optional<UserPreferencesEntity> userPreference = getUserPreference(key, userId);
        try {
            em.getTransaction().begin();
    
            userPreference.ifPresentOrElse(entity -> {
                entity.setPreferenceValue(value);
            }, () -> {
                UserPreferenceId userPreferenceId = new UserPreferenceId();
                userPreferenceId.setUserId(userId);
                userPreferenceId.setPreferenceKey(key.key());
                
                UserPreferencesEntity entity = new UserPreferencesEntity();
                entity.setPreferenceValue(value);
                entity.setId(userPreferenceId);
                entity.setDataType(dataType);
                
                em.persist(entity);
            });
            
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
}
