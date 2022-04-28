package si.smrpo.scrum.utils;

public class NumberUtils {
    
    private NumberUtils() {
    
    }
    
    public static double roundToQuarter(double number) {
        return Math.round(number * 4 ) / 4f;
    }
    
}
