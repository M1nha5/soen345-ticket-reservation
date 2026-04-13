package com.harjot.ticketreservation.service;

import com.harjot.ticketreservation.model.EventItem;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReservationPolicyServiceTest {
    private final ReservationPolicyService service = new ReservationPolicyService();

    @Test
    public void canReserveWhenCapacityIsEnough() {
        EventItem event = new EventItem("1", "Concert", "Music", "Montreal", 1L, 100, 10, "org1", "active", 0);
        assertTrue(service.canReserve(event, 3));
    }

    @Test
    public void cannotReserveWhenCapacityIsNotEnough() {
        EventItem event = new EventItem("1", "Concert", "Music", "Montreal", 1L, 100, 2, "org1", "active", 0);
        assertFalse(service.canReserve(event, 5));
    }

    @Test
    public void cannotReserveWithInvalidTicketCount() {
        EventItem event = new EventItem("1", "Concert", "Music", "Montreal", 1L, 100, 10, "org1", "active", 0);
        assertFalse(service.canReserve(event, 0));
    }
}
