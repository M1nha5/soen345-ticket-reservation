package com.harjot.ticketreservation.service;

import com.harjot.ticketreservation.model.EventItem;

import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventFilterServiceTest {
    private final EventFilterService service = new EventFilterService();

    @Test
    public void filterByLocationAndCategoryReturnsMatches() {
        long dateTime = toMillis("2026-04-12", "20:00");
        EventItem a = new EventItem("1", "Metal Show", "Concert", "Montreal", dateTime, 100, 100, "org1", "active", 0);
        EventItem b = new EventItem("2", "Jazz Show", "Concert", "Toronto", dateTime, 100, 100, "org1", "active", 0);
        List<EventItem> output = service.filter(Arrays.asList(a, b), "", "", "montreal", "concert");
        assertEquals(1, output.size());
        assertEquals("1", output.get(0).getId());
    }

    @Test
    public void filterByDateMatchesExactDay() {
        EventItem a = new EventItem("1", "Movie", "Cinema", "Montreal", toMillis("2026-04-12", "20:00"), 100, 100, "org1", "active", 0);
        List<EventItem> output = service.filter(Arrays.asList(a), "", "2026-04-12", "", "");
        assertEquals(1, output.size());
    }

    private long toMillis(String date, String time) {
        try {
            return com.harjot.ticketreservation.util.DateUtils.toEpochMillis(date, time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
