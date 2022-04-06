package si.smrpo.scrum.integrations.preferences.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.BadRequestException;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.integrations.preferences.UserPreferenceKey;
import si.smrpo.scrum.integrations.preferences.UserPreferences;
import si.smrpo.scrum.lib.UserPreference;
import si.smrpo.scrum.lib.enums.DataType;
import si.smrpo.scrum.persistence.identifiers.UserPreferenceId;
import si.smrpo.scrum.persistence.users.PreferenceTemplateEntity;
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
import java.util.Set;

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
    public Map<String, UserPreference> getUserPreferences(Set<String> keys, String userId) {
        TypedQuery<UserPreferencesEntity> query = em.createNamedQuery(UserPreferencesEntity.GET_BY_KEYS_AND_USER, UserPreferencesEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("keys", keys);
        
        return query.getResultStream()
            .collect(toMap(UserPreferencesEntity::getPreferenceKey, p -> {
                UserPreference pref = new UserPreference();
                pref.setValue(p.getPreferenceValue());
                pref.setUserId(p.getUserId());
                pref.setKey(p.getPreferenceKey());
                pref.setDataType(p.getDataType());
                return pref;
            }));
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
            .filter(pref -> pref.getDataType().equals(DataType.INTEGER))
            .map(UserPreferencesEntity::getPreferenceValue)
            .map(Integer::parseInt);
    }
    
    @Override
    public Optional<Boolean> getBoolean(UserPreferenceKey key, String userId) {
        return getUserPreference(key, userId)
            .filter(pref -> pref.getDataType().equals(DataType.BOOLEAN))
            .map(UserPreferencesEntity::getPreferenceValue)
            .map(Boolean::parseBoolean);
    }
    
    @Override
    public Optional<Double> getDecimal(UserPreferenceKey key, String userId) {
        return getUserPreference(key, userId)
            .filter(pref -> pref.getDataType().equals(DataType.FLOAT))
            .map(UserPreferencesEntity::getPreferenceValue)
            .map(Double::parseDouble);
    }
    
    @Override
    public Optional<JsonNode> getJSON(UserPreferenceKey key, String userId) {
        return getUserPreference(key, userId)
            .filter(pref -> pref.getDataType().equals(DataType.JSON))
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
        persistUserPreference(key, value, userId);
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, Long value, String userId) {
        persistUserPreference(key, value.toString(), userId);
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, Boolean value, String userId) {
        persistUserPreference(key, value.toString(), userId);
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, Double value, String userId) {
        persistUserPreference(key, value.toString(), userId);
    }
    
    @Override
    public void updateUserPreference(UserPreferenceKey key, ObjectNode value, String userId) {
        try {
            String stringifiedJson = objectMapper.writeValueAsString(value);
            persistUserPreference(key, stringifiedJson, userId);
        } catch (JsonProcessingException e) {
            LOG.error(e);
            throw new IllegalArgumentException("Unable to serialize JSON value!");
        }
    }
    
    @Override
    public void updateUserPreference(UserPreference userPreference, String userId) {
        try {
            UserPreferenceKey key = UserPreferenceKey.parse(userPreference.getKey());
            persistUserPreference(key, userPreference.getValue(), userId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("error.preferences.unknown-key");
        }
    }
    
    private void persistUserPreference(UserPreferenceKey key, String value, String userId) {
        Optional<UserPreferencesEntity> userPreference = getUserPreference(key, userId);
        try {
            em.getTransaction().begin();
            userPreference.ifPresentOrElse(entity -> {
                if (!validDataType(value, entity.getDataType())) {
                    throw new BadRequestException("Invalid data type for key '" + key.key() + "'! Required: '" + entity.getDataType().name() + "'.");
                }
                entity.setPreferenceValue(value);
            }, () -> {
                UserPreferencesEntity entity = getTemplatedPreference(key, value, userId);
                em.persist(entity);
            });
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    private UserPreferencesEntity getTemplatedPreference(UserPreferenceKey key, String value, String userId) {
        PreferenceTemplateEntity template = getTemplateEntity(key);
    
        UserPreferencesEntity entity = new UserPreferencesEntity();
        entity.setDataType(template.getDataType());
        UserPreferenceId id = new UserPreferenceId();
        id.setPreferenceKey(template.getPreferenceKey());
        id.setUserId(userId);
        entity.setId(id);
        
        if (value == null && template.getDefaultValue() == null) {
            throw new BadRequestException("error.bad-request");
        }
        
        String checkedValue = template.getDefaultValue();
        if (value != null && validDataType(value, template.getDataType())) {
            checkedValue = value;
        }
        entity.setPreferenceValue(checkedValue);
        
        return entity;
    }
    
    private PreferenceTemplateEntity getTemplateEntity(UserPreferenceKey key) {
        TypedQuery<PreferenceTemplateEntity> query = em.createNamedQuery(PreferenceTemplateEntity.GET_BY_KEY, PreferenceTemplateEntity.class);
        query.setParameter("key", key.key());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("Unrecognized preference key! Given key '" + key.key() + "' is not supported by the system.");
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private boolean validDataType(String value, DataType type) {
        try {
            switch (type) {
                case FLOAT:
                    Double.parseDouble(value);
                    return true;
                case INTEGER:
                    Integer.parseInt(value);
                    return true;
                case BOOLEAN:
                    return value.equals("true") || value.equals("false");
                case JSON:
                    objectMapper.readTree(value);
                    return true;
                default:
                case STRING:
                    return true;
            }
        } catch (NumberFormatException | JsonProcessingException e) {
            return false;
        }
    }
}
