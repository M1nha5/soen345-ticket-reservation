package com.harjot.ticketreservation.service;

import com.harjot.ticketreservation.model.EventItem;
import com.harjot.ticketreservation.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventFilterService {
    public List<EventItem> filter(List<EventItem> source, String query, String date, String location, String category) {
        List<EventItem> result = new ArrayList<>();
        String safeQuery = normalize(query);
        String safeDate = normalize(date);
        String safeLocation = normalize(location);
        String safeCategory = normalize(category);
        for (EventItem event : source) {
            if (event == null) {
                continue;
            }
            String title = normalize(event.getTitle());
            String eventLocation = normalize(event.getLocation());
            String eventCategory = normalize(event.getCategory());
            String eventDate = DateUtils.dateOnly(event.getDateTimeMillis());
            boolean matches = title.contains(safeQuery)
                    && eventLocation.contains(safeLocation)
                    && eventCategory.contains(safeCategory)
                    && eventDate.contains(safeDate);
            if (matches) {
                result.add(event);
            }
        }
        return result;
    }

    private String normalize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().toLowerCase(Locale.US);
    }
}
