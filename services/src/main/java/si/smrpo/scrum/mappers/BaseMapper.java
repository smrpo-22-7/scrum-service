package si.smrpo.scrum.mappers;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.persistence.BaseEntity;

public class BaseMapper {
    
    private BaseMapper() {
    
    }
    
    public static <T extends BaseType, E extends BaseEntity> T fromEntity(E entity, Class<T> typeClass) {
        try {
            T instance = typeClass.getDeclaredConstructor().newInstance();
            return fromEntity(entity, instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T extends BaseType, E extends BaseEntity> T fromEntity(E entity, T type) {
        type.setId(entity.getId());
        if (entity.getTimestamp() != null) {
            type.setTimestamp(entity.getTimestamp());
        }
        return type;
    }
    
}