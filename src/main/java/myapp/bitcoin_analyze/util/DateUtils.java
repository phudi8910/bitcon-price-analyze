package myapp.bitcoin_analyze.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
@Configuration
public class DateUtils {
    @Getter
    private static String timeZone;

    @Value("${spring.jpa.properties.hibernate.jdbc.time_zone}")
    public void setTimeZone(final String timeZone) {
        DateUtils.timeZone = timeZone;
    }

    public static Instant instantCurrentDateTime() {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of(DateUtils.timeZone));
        return dateTime.toInstant();
    }
}
