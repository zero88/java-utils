package io.github.zero88.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateTimes {

    private DateTimes() {}

    private static final Logger logger = LoggerFactory.getLogger(DateTimes.class);

    public static boolean isRelatedToDateTime(@NotNull Class cls) {
        return TemporalAccessor.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls) ||
               Calendar.class.isAssignableFrom(cls) || TimeZone.class.isAssignableFrom(cls);
    }

    public static LocalDateTime nowUTC() {
        return fromUTC(Instant.now());
    }

    public static LocalDateTime fromUTC(@NotNull Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static ZonedDateTime toUTC(@NotNull Date date) {
        return DateTimes.toUTC(date.toInstant());
    }

    public static ZonedDateTime toUTC(@NotNull Instant date) {
        return DateTimes.toUTC(date.atZone(ZoneId.systemDefault()));
    }

    public static ZonedDateTime toUTC(@NotNull LocalDateTime time) {
        return DateTimes.toUTC(time, ZoneId.systemDefault());
    }

    public static ZonedDateTime toUTC(@NotNull LocalDateTime time, @NotNull ZoneId zoneId) {
        return DateTimes.toUTC(time.atZone(zoneId));
    }

    public static ZonedDateTime toUTC(@NotNull ZonedDateTime dateTime) {
        return DateTimes.toZone(dateTime, ZoneOffset.UTC);
    }

    public static ZonedDateTime toZone(@NotNull ZonedDateTime dateTime, @NotNull ZoneId toZone) {
        return dateTime.withZoneSameInstant(toZone);
    }

    public static OffsetDateTime now() {
        return from(Instant.now());
    }

    public static long nowMilli() {
        return Instant.now().toEpochMilli();
    }

    public static OffsetDateTime from(@NotNull Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Utilities class for parsing {@code date/time/datetime} in {@code iso8601} to appropriate {@code java data type}
     *
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
     */
    public static class Iso8601Parser {

        public static Instant parse(@NotNull String datetime) {
            return Instant.from(parseFromISO8601(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        public static ZonedDateTime parseZonedDateTime(@NotNull String datetime) {
            return ZonedDateTime.from(parseFromISO8601(datetime, DateTimeFormatter.ISO_ZONED_DATE_TIME));
        }

        public static OffsetDateTime parseDateTime(@NotNull String datetime) {
            return OffsetDateTime.from(parseFromISO8601(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        public static OffsetDateTime parseDate(@NotNull String date) {
            return OffsetDateTime.from(parseFromISO8601(date, DateTimeFormatter.ISO_OFFSET_DATE));
        }

        public static OffsetTime parseTime(@NotNull String time) {
            return OffsetTime.from(parseFromISO8601(time, DateTimeFormatter.ISO_TIME));
        }

        private static TemporalAccessor parseFromISO8601(String datetime, @NotNull DateTimeFormatter formatter) {
            try {
                return formatter.parse(Strings.requireNotBlank(datetime));
            } catch (DateTimeParseException e) {
                logger.debug("Invalid date: " + datetime, e);
                throw new IllegalArgumentException("Invalid date", e);
            }
        }

        public static Object parse(@NotNull Class<?> cls, String value) {
            if (Strings.isBlank(value)) {
                return null;
            }
            if (!isRelatedToDateTime(cls)) {
                return value;
            }
            if (Instant.class.isAssignableFrom(cls)) {
                return parse(value);
            }
            if (OffsetDateTime.class.isAssignableFrom(cls)) {
                return parseDateTime(value);
            }
            if (ZonedDateTime.class.isAssignableFrom(cls)) {
                return parseZonedDateTime(value);
            }
            if (OffsetTime.class.isAssignableFrom(cls)) {
                return parseZonedDateTime(value);
            }
            if (java.sql.Date.class.isAssignableFrom(cls)) {
                return new java.sql.Date(parse(value).toEpochMilli());
            }
            if (Time.class.isAssignableFrom(cls)) {
                return Time.valueOf(value);
            }
            if (Timestamp.class.isAssignableFrom(cls)) {
                return new Timestamp(parse(value).toEpochMilli());
            }
            if (Date.class.isAssignableFrom(cls)) {
                return Date.from(parse(value));
            }
            return value;
        }

    }


    /**
     * Utilities class for formatting {@code date/time/datetime} in {@code java data type} to {@code iso8601}
     *
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601">ISO_8601</a>
     */
    public static class Iso8601Formatter {

        public static String formatDate(@NotNull ZonedDateTime zonedDate) {
            return zonedDate.format(DateTimeFormatter.ISO_OFFSET_DATE);
        }

        public static String formatDate(@NotNull OffsetDateTime offsetDate) {
            return offsetDate.format(DateTimeFormatter.ISO_OFFSET_DATE);
        }

        public static String formatTime(@NotNull OffsetTime value) {
            return DateTimeFormatter.ISO_TIME.format(value);
        }

        public static String format(@NotNull ZonedDateTime zonedDateTime) {
            return zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        }

        public static String format(@NotNull OffsetDateTime offsetDateTime) {
            return offsetDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

    }

}
