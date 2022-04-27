package si.smrpo.scrum.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    
    public static Instant truncateTime(Instant date) {
        return LocalDateTime.ofInstant(date, ZoneId.of("UTC"))
            .truncatedTo(ChronoUnit.DAYS).toInstant(ZoneOffset.UTC);
    }
    
    public static Date truncateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar. MILLISECOND, 0);
        return calendar.getTime();
    }
    
    public static double getQuarterHourDiff(Date date1, Date date2) {
        long diff = date2.getTime() - date1.getTime();
        double hours = (double)diff / 1000 / 60 / 60;
        return Math.round(hours * 4 ) / 4f;
    }
}
