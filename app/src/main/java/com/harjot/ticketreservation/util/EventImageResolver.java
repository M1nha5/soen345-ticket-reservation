package com.harjot.ticketreservation.util;

import com.harjot.ticketreservation.R;

import java.util.Locale;

public final class EventImageResolver {
    private EventImageResolver() {
    }

    public static int resolveByCategory(String category) {
        if (category == null) {
            return R.drawable.ph_event_default;
        }
        String value = category.trim().toLowerCase(Locale.US);
        if (value.contains("concert") || value.contains("music")) {
            return R.drawable.ph_event_concert;
        }
        if (value.contains("movie") || value.contains("cinema") || value.contains("film")) {
            return R.drawable.ph_event_movie;
        }
        if (value.contains("sport")) {
            return R.drawable.ph_event_sports;
        }
        if (value.contains("travel") || value.contains("trip")) {
            return R.drawable.ph_event_travel;
        }
        return R.drawable.ph_event_default;
    }
}
