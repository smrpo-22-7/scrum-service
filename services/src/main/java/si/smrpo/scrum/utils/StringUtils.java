package si.smrpo.scrum.utils;

public class StringUtils {
    
    private StringUtils() {
    
    }
    
    public static String removeSpaces(String value) {
        return removeSpaces(value, "");
    }
    
    public static String removeSpaces(String value, String delimitor) {
        return value.replaceAll(" +", " ").replaceAll(" ", delimitor);
    }
    
}
