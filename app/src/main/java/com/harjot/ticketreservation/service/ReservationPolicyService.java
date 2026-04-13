package com.harjot.ticketreservation.service;

import com.harjot.ticketreservation.model.EventItem;

public class ReservationPolicyService {
    public boolean canReserve(EventItem event, int requestedTickets) {
        if (event == null) {
            return false;
        }
        if (requestedTickets <= 0) {
            return false;
        }
        return event.getAvailableTickets() >= requestedTickets;
    }
}
