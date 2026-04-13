package com.harjot.ticketreservation.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private DateUtils() {
    }

    public static long toEpochMillis(String date, String time) throws ParseException {
        Date parsed = DATE_TIME_FORMAT.parse(date + " " + time);
        if (parsed == null) {
            throw new ParseException("Invalid date", 0);
        }
        return parsed.getTime();
    }

    public static String dateOnly(long epochMillis) {
        return DATE_FORMAT.format(new Date(epochMillis));
    }

    public static String dateTime(long epochMillis) {
        return DATE_TIME_FORMAT.format(new Date(epochMillis));
    }
}
