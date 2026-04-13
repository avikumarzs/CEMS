package models;

import java.sql.Date;

public class Event {
    private String eventId;
    private String title;
    private Date eventDate;
    private String status;
    private String organizerId;
    private int currentRegistrations;
    private String venueId;

    // Constructor
    public Event(String eventId, String title, Date eventDate, String status, int currentRegistrations, String venueId) {
        this.eventId = eventId;
        this.title = title;
        this.eventDate = eventDate;
        this.status = status;
        this.currentRegistrations = currentRegistrations;
        this.venueId = venueId;
    }
    // Constructor for the Dashboard Table Views
    public Event(String eventId, String title, java.sql.Date eventDate, String venueId, String organizerId, String status, int currentRegistrations) {
        this.eventId = eventId;
        this.title = title;
        this.eventDate = eventDate;
        this.venueId = venueId;
        this.organizerId = organizerId;
        this.status = status;
        this.currentRegistrations = currentRegistrations;
    }

    // Getters
    // --- ADD THESE AT THE BOTTOM ---
    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
    // -------------------------------
    public String getEventId() { return eventId; }
    public String getTitle() { return title; }
    public Date getEventDate() { return eventDate; }
    public String getStatus() { return status; }
    public int getCurrentRegistrations() { return currentRegistrations; }
    public String getVenueId() { return venueId; }
}