package si.smrpo.scrum.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    
    public static Instant truncateTime(Instant date) {
        return LocalDateTime.ofInstant(date, ZoneId.of("UTC"))
            .truncatedTo(ChronoUnit.DAYS).toInstant(ZoneOffset.UTC);
    }
    
}
