package com.harjot.ticketreservation.service;

import com.harjot.ticketreservation.model.EventItem;

import org.junit.Test;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookingFlowComponentTest {
    private final EventFilterService filterService = new EventFilterService();
    private final ReservationPolicyService reservationPolicyService = new ReservationPolicyService();

    @Test
    public void filteredEventCanBeReservedWhenAvailable() {
        EventItem event = new EventItem("1", "Rock Night", "Concert", "Montreal", toMillis("2026-04-12", "20:00"), 100, 25, "org1", "active", 0);
        List<EventItem> filtered = filterService.filter(Collections.singletonList(event), "rock", "2026-04-12", "montreal", "concert");
        assertEquals(1, filtered.size());
        assertTrue(reservationPolicyService.canReserve(filtered.get(0), 2));
    }

    private long toMillis(String date, String time) {
        try {
            return com.harjot.ticketreservation.util.DateUtils.toEpochMillis(date, time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
