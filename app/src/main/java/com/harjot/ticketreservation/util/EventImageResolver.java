package com.harjot.ticketreservation.util;

import com.harjot.ticketreservation.R;

public final class EventImageResolver {
    private static final int[] GRADIENTS = new int[]{
            R.drawable.ph_event_concert,
            R.drawable.ph_event_movie,
            R.drawable.ph_event_sports,
            R.drawable.ph_event_travel,
            R.drawable.ph_event_default
    };

    private EventImageResolver() {
    }

    public static int resolveByPosition(int position) {
        int index = Math.abs(position) % GRADIENTS.length;
        return GRADIENTS[index];
    }
}
