package com.harjot.ticketreservation.model;

public class EventItem {
    private String id;
    private String title;
    private String category;
    private String location;
    private long dateTimeMillis;
    private int totalTickets;
    private int availableTickets;
    private String organizerId;
    private String status;
    private long createdAt;

    public EventItem() {
    }

    public EventItem(String id, String title, String category, String location, long dateTimeMillis, int totalTickets, int availableTickets, String organizerId, String status, long createdAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.location = location;
        this.dateTimeMillis = dateTimeMillis;
        this.totalTickets = totalTickets;
        this.availableTickets = availableTickets;
        this.organizerId = organizerId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDateTimeMillis() {
        return dateTimeMillis;
    }

    public void setDateTimeMillis(long dateTimeMillis) {
        this.dateTimeMillis = dateTimeMillis;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
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
