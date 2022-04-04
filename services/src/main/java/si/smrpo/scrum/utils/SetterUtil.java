package si.smrpo.scrum.utils;

import java.util.function.Consumer;

public class SetterUtil {
    
    private SetterUtil() {
    
    }
    
    public static <T> void setIfNotNull(T value, Consumer<T> setter, boolean allowBlank) {
        if (value != null) {
            if (value instanceof String && !allowBlank) {
                String stringValue = (String) value;
                if (!stringValue.trim().isBlank()) {
                    setter.accept(value);
                }
            } else {
                setter.accept(value);
            }
        }
    }
    
    public static <T> void setIfNotNull(T value, Consumer<T> setter) {
        setIfNotNull(value, setter, false);
    }
    
}
