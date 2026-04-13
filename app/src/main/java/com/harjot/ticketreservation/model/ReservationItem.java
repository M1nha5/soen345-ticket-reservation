package com.harjot.ticketreservation.model;

public class ReservationItem {
    private String id;
    private String userId;
    private String eventId;
    private String eventTitle;
    private String eventLocation;
    private long eventDateTimeMillis;
    private int tickets;
    private String status;
    private long createdAt;

    public ReservationItem() {
    }

    public ReservationItem(String id, String userId, String eventId, String eventTitle, String eventLocation, long eventDateTimeMillis, int tickets, String status, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.eventDateTimeMillis = eventDateTimeMillis;
        this.tickets = tickets;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public long getEventDateTimeMillis() {
        return eventDateTimeMillis;
    }

    public void setEventDateTimeMillis(long eventDateTimeMillis) {
        this.eventDateTimeMillis = eventDateTimeMillis;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
